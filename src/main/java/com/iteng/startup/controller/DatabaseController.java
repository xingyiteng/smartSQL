package com.iteng.startup.controller;

import com.iteng.startup.common.ResponseResult;
import com.iteng.startup.model.dto.connection.DbConnectionDto;
import com.iteng.startup.model.entity.TableInfo;
import com.iteng.startup.service.DatabaseService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/database")
public class DatabaseController extends CommonController {

    @Resource
    private DatabaseService databaseService;

    /**
     * 连接数据库
     * @param dto
     * @return
     * @throws Exception
     */
    @PostMapping("/connect")
    public ResponseResult<?> connect(@RequestBody DbConnectionDto dto) throws Exception {
        Map<String, List<String>> tables = databaseService.getTablesFromMySQL(dto);
        return ResponseResult.success(tables);
    }

    /**
     * 获取DDL
     */
    @GetMapping("/getTableDDL")
    public ResponseResult<?> getTableDDL(@RequestParam String databaseName, @RequestParam String tableName) {
        String ddl = databaseService.getTableDDL(databaseName, tableName);
        return ResponseResult.success(ddl, "OK");
    }
}