package com.br.queryprocessor.database;

public class Usuario implements Table {
    private int idUsuario;
    private String nome;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cep;
    private String uf;
    private String dataNascimento;

    public boolean hasColumn(String column) {
        switch (column) {
            case "idusuario":
            case "nome":
            case "logradouro":
            case "numero":
            case "bairro":
            case "cep":
            case "uf":
            case "datanascimento":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String[] getAllCollumns() {
        return new String[]{
                "idUsuario",
                "nome",
                "logradouro",
                "numero",
                "bairro",
                "cep",
                "uf",
                "dataNascimento"
        };
    }
}
