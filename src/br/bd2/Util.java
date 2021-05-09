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
            boolean hasColumn = dao.hasTableAndColumn(table, value);

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
