package com.br.queryprocessor.model;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    static private List<String> tableList;
    static private QueryProcessor queryProcessor;

    public static class Builder {
        private List<String> tableList = new ArrayList<>();
        private QueryProcessor queryProcessor = new QueryProcessor();

        public Builder setQueryProcessor(QueryProcessor queryProcessor) {
            this.queryProcessor = queryProcessor;
            return this;
        }

        public Builder addTableList(String table) {
            this.tableList.add(table);
            return this;
        }

        public Builder setTableList(List<String> tables) {
            this.tableList = tables;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }

    public Configuration(Builder builder) {
        Configuration.tableList = builder.tableList;
        Configuration.queryProcessor = builder.queryProcessor;

    }

    public static List<String> getTableList() {
        return tableList;
    }

    public static String[] getTableArray() {
        return tableList.toArray(String[]::new);
    }

    public static void addTableList(String table) {
        Configuration.tableList.add(table);
    }

    public static void setTableList(List<String> tableList) {
        Configuration.tableList = tableList;
    }

    public static QueryProcessor getQueryProcessor() {
        return queryProcessor;
    }

    public static void setQueryProcessor(QueryProcessor queryProcessor) {
        Configuration.queryProcessor = queryProcessor;
    }
}