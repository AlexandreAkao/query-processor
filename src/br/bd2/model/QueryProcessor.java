package br.bd2.model;

import br.bd2.Util;
import br.bd2.constants.Keywords;
import br.bd2.database.DAO;
import br.bd2.nfa.Lang;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QueryProcessor {
    private GenericGraph firstGraph;
    private DAO dao = new DAO();

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

    public void graphGenerator(List<String> separation) {
        List<String> columnSelect = new ArrayList<>();
        Map<String, Map<String, String>> tablesColumns = new HashMap<>();
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
                auxGraph = this.firstGraph;
            } else if (s.contains("where")) {
                String where = sep.replaceAll("where", "σ");
                String[] t = Arrays.stream(where.split("σ|not|and|or"))
                        .filter(value -> value != null && value.trim().length() > 0)
                        .map(String::trim)
                        .toArray(String[]::new);

                addTableOrColumn(t, tableSelect, tablesColumns);

                GenericGraph whereGraph = new GenericGraph(where, where);
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

                addTableOrColumn(a, tableSelect, tablesColumns);

                String join = tableSelect + " |X| " + tableJoin + " (" + condition + ")";
                GenericGraph joinGraph = new GenericGraph(join, "join");
                if (auxGraph != null) {
                    auxGraph.addGenericGraphList(joinGraph);
                }
                auxGraph = joinGraph;
            } else if (s.contains("order by")) {
                String orderBy = sep.replaceAll("order by", "t");
                GenericGraph orderByGraph = new GenericGraph(orderBy, "order by");
                if (auxGraph != null) {
                    auxGraph.addGenericGraphList(orderByGraph);
                }
                auxGraph = orderByGraph;
            } else if (s.contains("from")) {
                tableSelect = sep.split(" ")[1];

                boolean isCorrect = Util.verifyTableAndColumn(dao, columnSelect, tableSelect);
                if (!isCorrect) return;
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
                    return;
                }
                auxGraph.addGenericGraphList(new GenericGraph(table, "table"));
            }
        }

//        auxGraph = this.firstGraph;
//        GenericGraph aux2Graph = null;

//        while (auxGraph != null) {
//            auxGraph = auxGraph.getGenericGraphList()
//        }
        System.out.println("Query valida");
        printGraph(this.firstGraph);
        System.out.println("\n================================================");
        optimazeGraph(this.firstGraph, null);
    }

    private void optimazeGraph(GenericGraph auxGraph, GenericGraph auxPreviusGraph) {
        String a = auxPreviusGraph == null ? auxGraph.getAlgRelational() : auxGraph.getAlgRelational() + " => " + auxPreviusGraph.getAlgRelational();
        System.out.println(a);

        List<GenericGraph> auxListGraph = auxGraph.getGenericGraphList();

        if (auxListGraph != null) {
            for (GenericGraph g : auxListGraph) {
                optimazeGraph(g, auxGraph);
            }
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