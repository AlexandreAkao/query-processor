package br.bd2.model;

import br.bd2.constants.Keywords;
import br.bd2.database.DAO;
import br.bd2.nfa.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if (acceptQuery) {
            System.out.println("Query valida");
        } else {
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
        List<String> tables = new ArrayList<>();
        GenericGraph auxGraph = null;

        System.out.println("------------------------------------------------------------------");

        for (String sep : separation) {
            List<String> s = Arrays.asList(sep.split(" "));
            int fromJoin = s.contains("from") ? s.indexOf("from") : s.indexOf("join");

            if (s.contains("select")) {
                String select = sep.replaceAll("select", "π");

                this.firstGraph = new GenericGraph(select);
                auxGraph = this.firstGraph;
            } else if (s.contains("where")) {
                String where = sep.replaceAll("where", "σ");
                GenericGraph whereGraph = new GenericGraph(where);
                if (auxGraph != null) {
                    auxGraph.addGenericGraphList(whereGraph);
                }
                auxGraph = whereGraph;
            } else if (s.contains("join")) {
                String join = sep.replaceAll("join (\\w)+ on", "|x|");
                GenericGraph joinGraph = new GenericGraph(join);
                if (auxGraph != null) {
                    auxGraph.addGenericGraphList(joinGraph);
                }
                auxGraph = joinGraph;
            } else if (s.contains("order by")) {
                String orderBy = sep.replaceAll("order by", "t");
                GenericGraph orderByGraph = new GenericGraph(orderBy);
                if (auxGraph != null) {
                    auxGraph.addGenericGraphList(orderByGraph);
                }
                auxGraph = orderByGraph;
            }
            if (fromJoin >= 0) {
                tables.add(s.get(fromJoin + 1));
            }
        }

        for (String table : tables) {
            if (auxGraph != null) {
                auxGraph.addGenericGraphList(new GenericGraph(table));
            }
        }

        printGraph(this.firstGraph);
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