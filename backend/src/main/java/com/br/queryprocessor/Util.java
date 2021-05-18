package com.br.queryprocessor;

import com.br.queryprocessor.database.DAO;
import com.br.queryprocessor.model.GenericGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Util {

    public static boolean hasTable(String query) {
        String[] sep = query.split(" ");

        return sep[0].contains(".");
    }

    public static boolean verifyTableAndColumn(DAO dao, List<String> columnSelect, String table) {
        for (String value : columnSelect) {
            String[] sep = value.split("\\.");

            boolean hasColumn;

            if (sep.length == 2) {
                hasColumn = dao.hasTableAndColumn(sep[0], sep[1]);;
            } else {
                hasColumn = dao.hasTableAndColumn(table, value);
            }

            if (!hasColumn) {
                System.out.println("Coluna [" + value + "] na tabela [" + table + "] nao existe ou Tabela nao existe");
                return false;
            }
        }
        return true;
    }

    public static boolean verifyTable(DAO dao, String table) {
        return dao.hasTable(table);
    }

    public static List<GenericGraph> sortList(List<GenericGraph> graphList) {
        List<GenericGraph> sortList = new ArrayList<>();

        graphList.sort(Comparator.comparingInt(Util::countConditions).reversed());

        sortList.addAll(graphList);

        return sortList;
    }

    public static int countConditions(GenericGraph graph) {
        GenericGraph auxGraph = graph;
        int count = 0;

        while (auxGraph != null) {
            String type = auxGraph.getType();
            if (type.equals("where")) {
                String algRelational = auxGraph.getAlgRelational();
                String[] conditions = Arrays.stream(algRelational.split("σ|not|and|or"))
                        .filter(value -> value != null && value.trim().length() > 0)
                        .map(String::trim)
                        .toArray(String[]::new);

                return conditions.length;
            }
            auxGraph = auxGraph.getGenericGraphList() == null ? null : auxGraph.getGenericGraphList().get(0);
        }

        return count;
    }
}
