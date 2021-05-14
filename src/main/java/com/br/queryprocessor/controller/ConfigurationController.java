package com.br.queryprocessor.controller;

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
    @PostMapping
    public ResponseEntity create(@RequestBody RequestConfiguration requestConfiguration) {
        Configuration.setTableList(requestConfiguration.getTableList());

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("table-list", Configuration.getTableArray());

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity show() {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("table-list", Configuration.getTableArray());

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
