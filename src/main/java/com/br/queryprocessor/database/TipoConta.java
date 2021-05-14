package com.br.queryprocessor.database;

public class TipoConta implements Table {
    private int idTipoConta;
    private String descricao;

    public boolean hasColumn(String column) {
        switch (column) {
            case "idtipoconta":
            case "descricao":
                return true;
            default:
                return false;
        }
    }
}
