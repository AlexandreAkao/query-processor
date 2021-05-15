package com.br.queryprocessor.database;

public class Categoria implements Table {
    private int idCategoria;
    private String descCategoria;

    public boolean hasColumn(String column) {
        switch (column) {
            case "idcategoria":
            case "desccategoria":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String[] getAllCollumns() {
        return new String[]{"idCategoria", "descCategoria"};
    }
}
