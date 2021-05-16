package com.br.queryprocessor.controller;

import com.br.queryprocessor.model.Configuration;
import com.br.queryprocessor.model.GenericGraph;
import com.br.queryprocessor.model.QueryProcessor;
import com.br.queryprocessor.model.RequestQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/query")
public class QueryProcessorController {
    @CrossOrigin
    @PostMapping
    public ResponseEntity queryGenerator(@RequestBody RequestQuery query) {
        String[] tables = Configuration.getTableArray();
        QueryProcessor queryProcessor = Configuration.getQueryProcessor();
        Map<String, Object> res = new LinkedHashMap<>();

        List<String> s = queryProcessor.parse(query.getQuery().toLowerCase(), tables);

        if (s == null) {
            res.put("error", "Query invalida");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(res);
        }

        GenericGraph gg = queryProcessor.graphGenerator(s);

        if (gg == null) {
            res.put("error", "Coluna ou tabela invalida");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(res);
        }

        res.put("table-list", Configuration.getTableArray());
        res.put("query", query.getQuery());
        res.put("query-tree", gg);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
