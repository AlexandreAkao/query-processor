package com.br.queryprocessor.database;

public interface Table {
    boolean hasColumn(String column);

    String[] getAllCollumns();
}
