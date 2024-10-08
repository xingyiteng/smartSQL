package com.iteng.startup.service.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSON;
import com.iteng.startup.model.entity.TableInfo;
import com.iteng.startup.service.DatabaseService;
import com.iteng.startup.service.SparkAIService;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.exception.SparkException;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SparkAIServiceImpl implements SparkAIService {
    public static final String hostUrl = "https://spark-api.xf-yun.com/v3.5/chat";
    public static final String appid = "ca555eee";
    public static final String apiSecret = "NGRjNmI1Yzg5MzRlM2JlZDhiNTQzMjkz";
    public static final String apiKey = "d8a1af241d27b5fd8357e89f2bf7821c";

    @Resource
    private DatabaseService databaseService;

    @Override
    public String chat(String msg, List<TableInfo> lists) {
        String res = "";
        StringBuilder ddl = new StringBuilder();
        if (!lists.isEmpty()) {
            for (TableInfo list : lists) {
                String tableDDL = databaseService.getTableDDL(list.getDatabaseName(), list.getTableName());
                ddl.append(tableDDL);
                ddl.append("\n");
            }
        }
        SparkClient sparkClient = new SparkClient();
        // 设置认证信息
        sparkClient.appid = "ca555eee";
        sparkClient.apiKey = "d8a1af241d27b5fd8357e89f2bf7821c";
        sparkClient.apiSecret = "NGRjNmI1Yzg5MzRlM2JlZDhiNTQzMjkz";
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent("我会给你MySQL数据库的DDL语句，以及要实现的SQL功能，格式如下：\n" +
                "{{DDL}}\n" +
                "{{实现诉求}}\n" +
                "你要根据DDL语句和实现诉求，只返回对应的SQL语句，其他信息都不要返回，包括注释、符号等。如果我没有给你提供新的DDL语句，请继续参考上一个DDL语句"));
        messages.add(SparkMessage.userContent(ddl +msg));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                // 指定请求版本，默认使用最新3.0版本
                .apiVersion(SparkApiVersion.V3_5)
                .build();

        try {
            // 同步调用
            SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
            String sql = extractSqlQuery(chatResponse.getContent());
            if(!isValidSqlStatement(sql)){
                res += "生成SQL格式错误：";
            }
            res+=sql;
        } catch (SparkException e) {
            System.out.println("发生异常了：" + e.getMessage());
        }
        return res;
    }

    @Override
    public String executeSql(String sql, String databaseName) {
        String url = "jdbc:mysql://localhost:3306/" + databaseName;
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // 判断SQL语句类型
            String[] sqlParts = sql.trim().toLowerCase().split("\\s+", 2);
            String sqlType = sqlParts[0].toUpperCase();

            switch (sqlType) {
                case "SELECT":
                    return handleSelectQuery(stmt, sql);
                case "INSERT":
                case "UPDATE":
                case "DELETE":
                    int affectedRows = stmt.executeUpdate(sql);
                    Map<String, Integer> result = new HashMap<>();
                    result.put("affect_row", affectedRows);
                    return JSON.toJSONString(result);
                case "ALTER":
                case "CREATE":
                case "DROP":
                    stmt.execute(sql);
                    Map<String, String> ddlResult = new HashMap<>();
                    ddlResult.put("message", "DDL操作执行成功");
                    return JSON.toJSONString(ddlResult);
                case "EXPLAIN":
                    return handleExplainQuery(stmt, sql);
                default:
                    throw new SQLException("不支持的SQL操作类型");
            }
        } catch (SQLException e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "执行SQL时发生错误: " + e.getMessage());
            return JSON.toJSONString(errorMap);
        }
    }

    @Override
    public String optimizeSql(String sql, String databaseName) {
        String res = "";
        String sqlRes = executeSql("EXPLAIN " + sql, databaseName);

        SparkClient sparkClient = new SparkClient();
        // 设置认证信息
        sparkClient.appid = "ca555eee";
        sparkClient.apiKey = "d8a1af241d27b5fd8357e89f2bf7821c";
        sparkClient.apiSecret = "NGRjNmI1Yzg5MzRlM2JlZDhiNTQzMjkz";
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent("我会给你一个SQL操作语句，以及MySQL数据库通过EXPLAIN关键字分析之后的结果，格式如下：\n" +
                "{{SQL}}\n" +
                "{{EXPLAIN分析结果}}\n" +
                "你要根据EXPLAIN结果分析各个属性的情况，请按以下格式输出内容：\n" +
                "id:{{id结果含义的分析说明}}\n" +
                "select_type: {{select_type结果含义的分析说明}}\n" +
                "table: {{table结果含义的分析说明}}\n" +
                "partitions:{{partitions结果含义的分析说明}}\n" +
                "type:{{type结果含义的分析说明}}\n" +
                "possible_keys: {{possible_keys结果含义的分析说明}}\n" +
                "key: {{key结果含义的分析说明}}\n" +
                "key_len: {{key_len结果含义的分析说明}}\n" +
                "ref: {{ref结果含义的分析说明}}\n" +
                "rows: {{rows结果含义的分析说明}}\n" +
                "filtered:{{filtered结果含义的分析说明}}\n" +
                "Extra: {{Extra结果含义的分析说明}}\n" +
                "结论：{{根据以上属性和SQL语句，分析索引命中情况、是否使用全表扫描。如果没有命中索引，请给出具体优化操作，如：是否需要新建索引或优化查询条件等等，并给出具体SQL操作语句}}"));
        messages.add(SparkMessage.userContent(sql+sqlRes));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                // 指定请求版本，默认使用最新3.0版本
                .apiVersion(SparkApiVersion.V3_5)
                .build();

        try {
            // 同步调用
            SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
            res = chatResponse.getContent();
        } catch (SparkException e) {
            System.out.println("发生异常了：" + e.getMessage());
        }
        return res;
    }

    @Override
    public String explainSql(String sql, String databaseName) {
        return executeSql("EXPLAIN " + sql, databaseName);
    }

    private String handleSelectQuery(Statement stmt, String sql) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try (ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                resultList.add(row);
            }
        }

        return JSON.toJSONString(resultList);
    }

    private String handleExplainQuery(Statement stmt, String sql) throws SQLException {
        List<Map<String, Object>> explainResult = new ArrayList<>();

        try (ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                explainResult.add(row);
            }
        }

        return JSON.toJSONString(explainResult);
    }

    public static String extractSqlQuery(String input) {
        String regex = "(?i)(SELECT|INSERT|UPDATE|DELETE|CREATE|ALTER|DROP|TRUNCATE|MERGE)\\s+.*?;";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group().trim();
        }

        return null;
    }

    public static boolean isValidSqlStatement(String sqlStatement) {
        try {
            // 使用 Druid 解析 SQL 语句
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sqlStatement, JdbcConstants.MYSQL);

            // 如果能成功解析，则认为 SQL 语句有效
            if (stmtList.size() > 0) {
                // 可以进一步分析 SQL 语句
                SQLStatement stmt = stmtList.get(0);
                MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
                stmt.accept(visitor);

                // 打印一些解析信息，如表名、字段等
                // System.out.println("Tables : " + visitor.getTables());
                // System.out.println("Fields : " + visitor.getColumns());

                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("SQL 语法错误: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean terminateChat() {
        return false;
    }
}