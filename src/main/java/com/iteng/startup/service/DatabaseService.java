package com.iteng.startup.service;

import com.iteng.startup.model.dto.connection.DbConnectionDto;
import com.iteng.startup.model.entity.TableInfo;

import java.util.List;
import java.util.Map;

public interface DatabaseService {
    // 其他现有的方法声明...

    String getTableDDL(String databaseName, String tableName);
    Map<String, List<String>> getTablesFromMySQL(DbConnectionDto dto) throws Exception;
}
