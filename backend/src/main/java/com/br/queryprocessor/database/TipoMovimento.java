package com.br.queryprocessor.database;

public class TipoMovimento implements Table {
    private int idTipoMovimento;
    private String descMovimentacao;

    public boolean hasColumn(String column) {
        switch (column) {
            case "idtipomovimento":
            case "descmovimentacao":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String[] getAllCollumns() {
        return new String[]{
                "idTipoMovimento",
                "descMovimentacao"
        };
    }
}
