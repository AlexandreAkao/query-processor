package br.bd2.database;

public class DAO {
    private final Table Usuario = new Usuario();
    private final Table Contas = new Contas();
    private final Table Movimentacao = new Movimentacao();
    private final Table TipoConta = new TipoConta();
    private final Table TipoMovimento = new TipoMovimento();
    private final Table Categoria = new Categoria();

    public boolean hasTableAndColumn(String table, String column) {
        Table tableModel = getTable(table);

        if (tableModel == null) return false;
        return tableModel.hasColumn(column);
    }

    private Table getTable(String table) {
        switch (table) {
            case "usuario":
                return Usuario;
            case "contas":
                return Contas;
            case "movimentacao":
                return Movimentacao;
            case "tipoconta":
                return TipoConta;
            case "tipomovimento":
                return TipoMovimento;
            case "categoria":
                return Categoria;
            default:
                return null;
        }
    }
}
