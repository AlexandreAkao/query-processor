package br.bd2;

import br.bd2.model.QueryProcessor;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] tables = {
                "usuario",
                "contas",
                "tipoconta",
                "movimentacao",
                "tipomovimento",
                "categoria"
        };

        String query = "select a from categoria join usuario on a = a";
        QueryProcessor queryProcessor = new QueryProcessor();
        List<String> s = queryProcessor.parse(query, tables);

        queryProcessor.graphGenerator(s);
    }
}