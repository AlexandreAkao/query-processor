package com.br.queryprocessor;

import com.br.queryprocessor.model.Configuration;
import com.br.queryprocessor.model.QueryProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QueryProcessorApplication {

	public static void main(String[] args) {
		QueryProcessor queryProcessor = new QueryProcessor();

		new Configuration.Builder()
				.addTableList("usuario")
				.addTableList("contas")
				.addTableList("tipoconta")
				.addTableList("movimentacao")
				.addTableList("tipomovimento")
				.addTableList("categoria")
				.setQueryProcessor(queryProcessor)
				.build();

		SpringApplication.run(QueryProcessorApplication.class, args);
	}
}
