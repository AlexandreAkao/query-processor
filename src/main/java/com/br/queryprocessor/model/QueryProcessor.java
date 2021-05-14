package com.br.queryprocessor.model;

import com.br.queryprocessor.Util;
import com.br.queryprocessor.constants.Keywords;
import com.br.queryprocessor.database.DAO;
import com.br.queryprocessor.nfa.Lang;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QueryProcessor {
    private GenericGraph firstGraph;
    private final DAO dao = new DAO();
    private String tableDefault;

    public List<String> parse(String query, String[] tables) {
        String[] querySeparation = query.split(" ");
        List<String> separation = new ArrayList<>();
        StringBuilder acc = new StringBuilder();

        Lang lang = new Lang(tables);
        boolean acceptQuery = lang.getNfa().run(query);

        System.out.println(query);
        if (!acceptQuery) {
            System.out.println("Query invalida");
            return null;
        }
        System.out.println("------------------------------------------------------------------");

        String w = "";
        for (String word : querySeparation) {
            w = word.equals("by") && w.equals("order") ? "order by" : word;
            boolean flag = Keywords.hasEqual(w);

            if (word.equals("order")) continue;

            if (acc.toString().equals("")) {
                acc.append(w);
            } else if (flag) {
                separation.add(acc.toString());
                acc = new StringBuilder(w);
            } else {
                acc.append(" ").append(w);
            }
        }

        if (acc.length() > 0) separation.add(acc.toString());

        for (String s : separation) {
            System.out.println(s);
        }

        return separation;
    }

    public GenericGraph graphGenerator(List<String> separation) {
        List<String> columnSelect = new ArrayList<>();
        Map<String, Map<String, String>> tablesColumns = new HashMap<>();
        Map<String, Map<String, String>> whereConditions = new HashMap<>();
        Map<String, String> separationHash = new HashMap<>();
        String tableSelect = "";
        GenericGraph auxGraph = null;

        System.out.println("------------------------------------------------------------------");

        for (String sep : separation) {
            List<String> s = Arrays.asList(sep.split(" "));
            int fromJoin = s.contains("from") ? s.indexOf("from") : s.indexOf("join");

            if (s.contains("order") && s.contains("by")) {
                s = s.stream().filter(keyWord -> !keyWord.equals("order")).collect(Collectors.toList());
                s.set(0, "order by");
            }

            if (s.contains("select")) {
                String select = sep.replaceAll("select", "π");
                String[] separationSelect = select.split(" ");
                String[] subarray = IntStream.range(1, separationSelect.length)
                        .mapToObj(i -> separationSelect[i].replace(",", ""))
                        .toArray(String[]::new);

                columnSelect.addAll(Arrays.asList(subarray));

                this.firstGraph = new GenericGraph(select, "select");
                separationHash.put("select", select);
                auxGraph = this.firstGraph;
            } else if (s.contains("where")) {
                String where = sep.replaceAll("where", "σ");
                String[] conditions = Arrays.stream(where.split("σ|not|and|or"))
                        .filter(value -> value != null && value.trim().length() > 0)
                        .map(String::trim)
                        .toArray(String[]::new);

                String[] conditionsWhereList = Arrays.stream(where.split("σ|and|or"))
                        .filter(value -> value != null && value.trim().length() > 0)
                        .map(String::trim)
                        .toArray(String[]::new);

                String[] t = Arrays.stream(conditions)
                        .map(value -> value.split(" ")[0])
                        .toArray(String[]::new);

                boolean isCorrect = Util.verifyTableAndColumn(dao, Arrays.asList(t), tableSelect);
                if (!isCorrect) return null;

                addWhereConditions(conditionsWhereList, tableSelect, whereConditions);
                addTableOrColumn(t, tableSelect, tablesColumns);

                GenericGraph whereGraph = new GenericGraph(where, "where");
                separationHash.put("where", where);

                if (auxGraph != null) {
                    auxGraph.addGenericGraphList(whereGraph);
                }
                auxGraph = whereGraph;
            } else if (s.contains("join")) {
                String tableJoin = sep.split(" ")[1];
                String condition = sep.split("join (\\w)+ on")[1].trim();
                String[] a = Arrays.stream(condition.split("="))
                        .map(String::trim)
                        .toArray(String[]::new);

                boolean isCorrect = Util.verifyTableAndColumn(dao, Arrays.asList(a), tableSelect);
                if (!isCorrect) return null;

                addTableOrColumn(a, tableSelect, tablesColumns);

                String join = tableSelect + " |X| " + tableJoin + " (" + condition + ")";
                GenericGraph joinGraph = new GenericGraph(join, "join");
                separationHash.put("join", join);

                if (auxGraph != null) {
                    auxGraph.addGenericGraphList(joinGraph);
                }
                auxGraph = joinGraph;
            } else if (s.contains("order by")) {
                String orderBy = sep.replaceAll("order by", "t");
                String orderByWithoutOB = orderBy.substring(2);
                GenericGraph orderByGraph = new GenericGraph(orderBy, "order by");
                separationHash.put("order by", orderBy);

                String[] a = Arrays.stream(orderByWithoutOB.split(","))
                        .map(String::trim)
                        .map(value -> value.split(" ")[0])
                        .toArray(String[]::new);

                boolean isCorrect = Util.verifyTableAndColumn(dao, Arrays.asList(a), tableSelect);
                if (!isCorrect) return null;

                addTableOrColumn(a, tableSelect, tablesColumns);
                if (auxGraph != null) {
                    auxGraph.addGenericGraphList(orderByGraph);
                }
                auxGraph = orderByGraph;
            } else if (s.contains("from")) {
                tableSelect = sep.split(" ")[1];

                boolean isCorrect = Util.verifyTableAndColumn(dao, columnSelect, tableSelect);
                if (!isCorrect) return null;
                addTableOrColumn(columnSelect.toArray(String[]::new), tableSelect, tablesColumns);
            }
            if (fromJoin >= 0) {
                String table = s.get(fromJoin + 1);
                tablesColumns.computeIfAbsent(table, k -> new HashMap<>());
            }
        }

        for (Map.Entry<String, Map<String, String>> entry : tablesColumns.entrySet()) {
            if (auxGraph != null) {
                String table = entry.getKey();

                boolean isCorrect = Util.verifyTable(dao, table);
                if (!isCorrect) {
                    System.out.println("Query invalida");
                    return null;
                }
                auxGraph.addGenericGraphList(new GenericGraph(table, "table"));
            }
        }

        this.tableDefault = tableSelect;
        System.out.println("Query valida");
        printGraph(this.firstGraph);
        System.out.println("\n================================================");
        optimazeGraph(
                whereConditions,
                separationHash,
                tablesColumns
        );

        return this.firstGraph;
    }

    private void optimazeGraph(
            Map<String, Map<String, String>> whereConditions,
            Map<String, String> separationHash,
            Map<String, Map<String, String>> tablesColumns
    ) {
        GenericGraph graphRef = null;

        String orderBy = separationHash.get("order by");
        GenericGraph select = new GenericGraph(separationHash.get("select"), "select");

        if (orderBy != null) {
            this.firstGraph = new GenericGraph(orderBy, "order by");
            this.firstGraph.addGenericGraphList(select);
        } else {
            this.firstGraph = select;
        }

        graphRef = select;

        String where = separationHash.get("where");

        if (where != null) {
            String[] formatedConditions = retriveFormatedConditions(where);

            if (formatedConditions.length != 1) {
                Map<String, String> whereCond = new HashMap<>();
                for (String formatedConditionsSep : formatedConditions) {
                    String[] cond = Arrays.stream(formatedConditionsSep.split("and"))
                            .filter(value -> value != null && value.trim().length() > 0)
                            .map(String::trim)
                            .toArray(String[]::new);

                    if (cond.length > 1 && !this.allConditionSameTable(cond)) {
                        GenericGraph whereGraph = new GenericGraph("σ " + formatedConditionsSep, "where");
                        graphRef.addGenericGraphList(whereGraph);
                        graphRef = whereGraph;
                    } else {
                        String table = Util.hasTable(cond[0]) ? cond[0].split("\\.")[0] : this.tableDefault;

                        whereCond.merge(table, formatedConditionsSep, (a, b) -> a + " or " + b);
                    }
                }

                GenericGraph join = new GenericGraph(separationHash.get("join"), "join");
                graphRef.addGenericGraphList(join);
                graphRef = join;

                List<GenericGraph> tableList = new ArrayList<>();
                for (Map.Entry<String, Map<String, String>> entry : tablesColumns.entrySet()) {
                    List<String> columns = new ArrayList<>();
                    String table = entry.getKey();

                    for (Map.Entry<String, String> v : entry.getValue().entrySet()) {
                        columns.add(v.getValue());
                    }
                    String projection = "π " + String.join(", ", columns);

                    GenericGraph projectionGraph = new GenericGraph(projection, "projection");

                    String queryWhere = whereCond.get(table);

                    if (queryWhere != null) {
                        GenericGraph queryWhereGraph = new GenericGraph(queryWhere, "where");
                        projectionGraph.addGenericGraphList(queryWhereGraph);

                        GenericGraph tableGraph = new GenericGraph(table, "table");
                        queryWhereGraph.addGenericGraphList(tableGraph);
                    } else {
                        GenericGraph tableGraph = new GenericGraph(table, "table");
                        projectionGraph.addGenericGraphList(tableGraph);
                    }

                    tableList.add(projectionGraph);
                }

                graphRef.addGenericGraphList(tableList);
            } else {
                GenericGraph join = new GenericGraph(separationHash.get("join"), "join");
                graphRef.addGenericGraphList(join);
                graphRef = join;

                List<GenericGraph> tableList = new ArrayList<>();
                for (Map.Entry<String, Map<String, String>> entry : tablesColumns.entrySet()) {
                    List<String> columns = new ArrayList<>();
                    String table = entry.getKey();

                    for (Map.Entry<String, String> v : entry.getValue().entrySet()) {
                        columns.add(v.getValue());
                    }
                    String projection = "π " + String.join(", ", columns);

                    GenericGraph projectionGraph = new GenericGraph(projection, "projection");

                    Map<String, String> conditionsTable = whereConditions.get(table);

                    if (conditionsTable != null) {
                        List<String> conditionQueryList = new ArrayList<>();

                        for (Map.Entry<String, String> condition : conditionsTable.entrySet()) {
                            conditionQueryList.add(condition.getValue());
                        }

                        String conditionQuery = "σ " + String.join(" " + formatedConditions[0] + " ", columns);

                        GenericGraph queryWhereGraph = new GenericGraph(conditionQuery, "where");
                        projectionGraph.addGenericGraphList(queryWhereGraph);

                        GenericGraph tableGraph = new GenericGraph(table, "table");
                        queryWhereGraph.addGenericGraphList(tableGraph);
                    } else {
                        GenericGraph tableGraph = new GenericGraph(table, "table");
                        projectionGraph.addGenericGraphList(tableGraph);
                    }

                    tableList.add(projectionGraph);
                }

                graphRef.addGenericGraphList(tableList);
            }
        } else {
            GenericGraph join = new GenericGraph(separationHash.get("join"), "join");
            graphRef.addGenericGraphList(join);
            graphRef = join;

            List<GenericGraph> tableList = new ArrayList<>();
            for (Map.Entry<String, Map<String, String>> entry : tablesColumns.entrySet()) {
                List<String> columns = new ArrayList<>();
                String table = entry.getKey();

                for (Map.Entry<String, String> v : entry.getValue().entrySet()) {
                    columns.add(v.getValue());
                }
                String projection = "π " + String.join(", ", columns);

                GenericGraph projectionGraph = new GenericGraph(projection, "projection");

                GenericGraph tableGraph = new GenericGraph(table, "table");
                projectionGraph.addGenericGraphList(tableGraph);

                tableList.add(projectionGraph);
            }

            graphRef.addGenericGraphList(tableList);
        }
    }

    private void addTableOrColumn(String[] collumns, String defaultTable, Map<String, Map<String, String>> tablesColumns) {
        for (String value : collumns) {
            boolean hasTable = Util.hasTable(value);

            if (hasTable) {
                String[] aux = value.split(" ")[0].split("\\.");
                String tableCondition = aux[0];
                String columnCondition = aux[1];

                Map<String, String> tableHashMap = tablesColumns.get(tableCondition);

                if (tableHashMap == null) {
                    Map<String, String> columnsList = new HashMap<>();
                    columnsList.put(columnCondition, columnCondition);
                    tablesColumns.put(tableCondition, columnsList);
                } else {
                    tableHashMap.put(columnCondition, columnCondition);
                }
            } else {
                Map<String, String> tableHashMap = tablesColumns.get(defaultTable);
                String v = value.split(" ")[0];

                if (tableHashMap == null) {
                    Map<String, String> columnsList = new HashMap<>();
                    columnsList.put(v, v);
                    tablesColumns.put(defaultTable, columnsList);
                } else {
                    tableHashMap.put(v, v);
                }
            }
        }
    }

    private void addWhereConditions(String[] conditions, String defaultTable, Map<String, Map<String, String>> whereConditions) {
        for (String value : conditions) {
            String sep = value.split(" ")[0];

            boolean hasTable = Util.hasTable(sep);

            if (hasTable) {
                String table = sep.split("\\.")[0];

                Map<String, String> conditionHashMap = whereConditions.get(table);

                if (conditionHashMap == null) {
                    Map<String, String> conditionList = new HashMap<>();
                    conditionList.put(value, value);
                    whereConditions.put(table, conditionList);
                } else {
                    conditionHashMap.put(value, value);
                }
            } else {
                Map<String, String> conditionHashMap = whereConditions.get(defaultTable);

                if (conditionHashMap == null) {
                    Map<String, String> columnsList = new HashMap<>();
                    columnsList.put(value, value);
                    whereConditions.put(defaultTable, columnsList);
                } else {
                    conditionHashMap.put(value, value);
                }
            }
        }
    }

    private String[] retriveFormatedConditions(String where) {
        boolean and = where.contains(" and ");
        boolean or = where.contains(" or ");

        String[] allConditions = Arrays.stream(where.split("σ|not|and|or"))
                .filter(value -> value != null && value.trim().length() > 0)
                .map(String::trim)
                .toArray(String[]::new);

        if (and && or && !this.allConditionSameTable(allConditions)) {
            String[] a = Arrays.stream(where.split("σ|or"))
                    .filter(value -> value != null && value.trim().length() > 0)
                    .map(String::trim)
                    .toArray(String[]::new);

            List<String> queryWhere = new ArrayList<>();

            for (String cond : a) {
                String[] b = Arrays.stream(cond.split("not|and|or"))
                        .filter(value -> value != null && value.trim().length() > 0)
                        .map(String::trim)
                        .toArray(String[]::new);

                String table = Util.hasTable(b[0]) ? b[0].split("\\.")[0] : this.tableDefault;

                for (String condi : b) {
                    condi = Util.hasTable(condi) ? condi.split("\\.")[0] : this.tableDefault;

                    if (!table.equals(condi)) {
                        System.out.println("Mais de uma tabela");
                        break;
                    }
                }

                queryWhere.add(cond);
            }

            return queryWhere.toArray(String[]::new);
        } else {
//            if (or) {
//                return new String[]{"or"};
//            } else {
//                String[] a = Arrays.stream(where.split("σ|or|not|and"))
//                        .filter(value -> value != null && value.trim().length() > 0)
//                        .map(String::trim)
//                        .toArray(String[]::new);
//
//                String table = Util.hasTable(a[0]) ? a[0].split("\\.")[0] : this.tableDefault;
//
//                for (String cond : a) {
//                    cond = Util.hasTable(cond) ? cond.split("\\.")[0] : this.tableDefault;
//
//                    if (!table.equals(cond)) {
//                        System.out.println("Mais de uma tabela");
//                        return new String[]{"multi"};
//                    }
//                }
//
//                return new String[]{"and"};
//            }
            return and ? new String[]{"and"} : new String[]{"or"};
        }
    }

    private boolean allConditionSameTable(String[] conditions) {
        String table = Util.hasTable(conditions[0]) ? conditions[0].split("\\.")[0] : this.tableDefault;

        for (String condition : conditions) {
            condition = Util.hasTable(condition) ? condition.split("\\.")[0] : this.tableDefault;

            if (!condition.equals(table)) return false;
        }
        return true;
    }

    public void printGraph(GenericGraph firstGraph) {
        GenericGraph graph = firstGraph;

        while (graph != null) {
            System.out.print(graph.getAlgRelational() + " ");

            if (graph.getGenericGraphList() != null) {
                System.out.print("\n");

                for (int i = 0; i < graph.getGenericGraphList().size(); i++) {
                    printGraph(graph.getGenericGraphList().get(i));
                }
            }
            graph = null;
        }
    }
}