package br.bd2;

import br.bd2.database.DAO;

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
}
