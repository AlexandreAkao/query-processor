package br.bd2.database;

public class Movimentacao implements Table {
    private int idTipoConta;
    private String dataMovimentacao;
    private String descricao;
    private int TipoMovimento_idTipoMovimento;
    private int Categoria_idCategoria;
    private int Conta_idConta;
    private int valor;

    public boolean hasColumn(String column) {
        switch (column) {
            case "idtipoconta":
            case "datamovimentacao":
            case "descricao":
            case "tipomovimento_idtipomovimento":
            case "categoria_idcategoria":
            case "conta_idconta":
            case "valor":
                return true;
            default:
                return false;
        }
    }
}
