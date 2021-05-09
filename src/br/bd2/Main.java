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

//        String query = "select Nome from usuario join contas on usuario.idusuario = contas.usuario_idusuario where Nome = 'Alan and nome > 1'";
        String query = "SELECT Numero, Cep FROM Usuario JOIN Contas ON Usuario.idUsuario = Contas.Usuario_idUsuario WHERE Numero > 2 and Numero < 5 ORDER BY Nome ASC";
        QueryProcessor queryProcessor = new QueryProcessor();
        List<String> s = queryProcessor.parse(query.toLowerCase(), tables);

        queryProcessor.graphGenerator(s);
    }
}