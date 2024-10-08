package com.iteng.startup.service;

import com.iteng.startup.model.entity.TableInfo;

import java.util.List;

public interface SparkAIService {
    String chat(String message, List<TableInfo> lists) throws Exception;
    boolean terminateChat();

    String executeSql(String sql, String databaseName);

    String optimizeSql(String sql, String databaseName);

    String explainSql(String sql, String databaseName);
}