package com.iteng.startup.service.impl;

import com.iteng.startup.model.dto.connection.DbConnectionDto;
import com.iteng.startup.model.entity.TableInfo;
import com.iteng.startup.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, List<String>> getTablesFromMySQL(DbConnectionDto dto) throws Exception {
        // 构建连接 URL
        String url = "jdbc:mysql://" + dto.getHost() + ":" + dto.getPort() + "/information_schema?useSSL=false&characterEncoding=utf8&serverTimezone=UTC";

        // 创建一个新的 DataSource
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(dto.getUsername());
        dataSource.setPassword(dto.getPassword());

        // 创建一个新的 JdbcTemplate 实例
        JdbcTemplate template = new JdbcTemplate(dataSource);

        // 查询用户拥有权限的数据库
        List<String> databases = template.queryForList("SELECT schema_name FROM schemata", String.class);

        // 查询每个数据库中的表
        List<TableInfo> list = new ArrayList<>();
        for (String database : databases) {
            List<String> tables = template.queryForList(
                "SELECT table_name FROM tables WHERE table_schema = ?",
                String.class,
                database
            );
            for (String tableName : tables) {
                list.add(new TableInfo(database, tableName));
            }
        }
        Map<String, List<String>> res = list.stream().collect(Collectors.groupingBy(
                        TableInfo::getDatabaseName,
                        Collectors.mapping(TableInfo::getTableName, Collectors.toList())
                ));
        return res;
    }

    @Override
    public String getTableDDL(String databaseName, String tableName) {
        String sql = "SHOW CREATE TABLE " + databaseName + "." + tableName;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getString(2));
    }


    private String getTableDDLById(Long tableId) {
        // 这里需要实现根据tableId查询对应的数据库名和表名，然后调用getTableDDL方法
        // 这是一个示例实现，实际上你需要根据你的数据模型来实现
        String sql = "SELECT database_name, table_name FROM table_info WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            String databaseName = rs.getString("database_name");
            String tableName = rs.getString("table_name");
            return getTableDDL(databaseName, tableName);
        }, tableId);
    }
}
