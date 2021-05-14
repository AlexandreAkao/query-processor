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

        String query = "" +
                "SELECT Numero, Cep, contas.descricao " +
                "FROM Usuario " +
                "JOIN Contas ON Usuario.idUsuario = Contas.Usuario_idUsuario " +
                "WHERE contas.idConta > 2 and not nome = 'dAda' or numero > 5 and numero > 7 " +
//                "WHERE not nome = 'dAda' or numero > 5 and numero > 7 " +
                "ORDER BY Nome ASC, idusuario DESC";
        QueryProcessor queryProcessor = new QueryProcessor();
//        String query = "select Nome from usuario join contas on usuario.idusuario = contas.usuario_idusuario where Nome = 'Alan and nome > 1'";
        List<String> s = queryProcessor.parse(query.toLowerCase(), tables);

        queryProcessor.graphGenerator(s);
    }
}