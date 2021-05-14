package com.br.queryprocessor.database;

public class Contas implements Table {
    private int idConta;
    private String descricao;
    private int tipoConta_idTipoConta;
    private int usuario_idUsuario;
    private int saldoInicial;

    public boolean hasColumn(String column) {
        switch (column) {
            case "idconta":
            case "descricao":
            case "tipoconta_idtipoconta":
            case "usuario_idusuario":
            case "saldoinicial":
                return true;
            default:
                return false;
        }
    }
}
