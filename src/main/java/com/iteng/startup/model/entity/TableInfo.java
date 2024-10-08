package com.iteng.startup.model.entity;

import lombok.Data;

@Data
public class TableInfo {
    private String databaseName;
    private String tableName;

    public TableInfo(String databaseName, String tableName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
    }
}