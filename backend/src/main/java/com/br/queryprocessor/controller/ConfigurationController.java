package com.br.queryprocessor.controller;

import com.br.queryprocessor.database.DAO;
import com.br.queryprocessor.model.Configuration;
import com.br.queryprocessor.model.RequestConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {
    @CrossOrigin
    @PostMapping
    public ResponseEntity create(@RequestBody RequestConfiguration requestConfiguration) {
        Configuration.setTableList(requestConfiguration.getTableList());

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("table-list", Configuration.getTableArray());

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity show() {
        Map<String, Object> res = new LinkedHashMap<>();

        String[] tables = Configuration.getTableArray();

        Map<String, String[]> r = new DAO().getAllTable(tables);

        res.put("table-list", r);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
