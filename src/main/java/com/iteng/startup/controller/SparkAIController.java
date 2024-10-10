package com.iteng.startup.controller;

import com.iteng.startup.common.ResponseResult;
import com.iteng.startup.model.dto.spark.ExeDto;
import com.iteng.startup.model.dto.spark.GenDto;
import com.iteng.startup.model.entity.TableInfo;
import com.iteng.startup.service.SparkAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spark-ai")
public class SparkAIController {

    @Autowired
    private SparkAIService sparkAIService;

    /**
     * 生成sql
     * @throws Exception
     */
    @PostMapping("/gen-sql")
    public ResponseResult<?> genSql(@RequestBody GenDto dto) throws Exception {
        String sql = sparkAIService.genSql(dto.getMessage(), dto.getLists());
        return ResponseResult.success(sql, "OK");
    }

    /**
     * 执行sql
     */
    @PostMapping("/exe-sql")
    public ResponseResult<?> executeSql(@RequestBody ExeDto dto){
        String res = sparkAIService.executeSql(dto.getSql(), dto.getDatabaseName());
        return ResponseResult.success(res, "OK");
    }

    /**
     * sql优化
     */
    @PostMapping("/optimize-sql")
    public ResponseResult<?> optimizeSql(@RequestBody ExeDto dto){
        String res = sparkAIService.optimizeSql(dto.getSql(), dto.getDatabaseName());
        return ResponseResult.success(res, "OK");
    }

    /**
     * explain结果
     */
    @PostMapping("/explain-sql")
    public ResponseResult<?> explainSql(@RequestBody ExeDto dto){
        String res = sparkAIService.explainSql(dto.getSql(), dto.getDatabaseName());
        return ResponseResult.success(res, "OK");
    }

    /**
     * 结束连接
     * @return
     */
    @PostMapping("/terminate")
    public ResponseResult<?> terminateChat() {
        boolean res = sparkAIService.terminateChat();
        return ResponseResult.success(res, "OK");
    }
}