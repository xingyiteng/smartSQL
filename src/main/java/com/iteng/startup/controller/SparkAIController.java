package com.iteng.startup.controller;

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
    public String chat(@RequestBody GenDto dto) throws Exception {
        return sparkAIService.chat(dto.getMessage(), dto.getLists());
    }

    /**
     * 执行sql
     */
    @PostMapping("/exe-sql")
    public String executeSql(@RequestBody ExeDto dto){
        return sparkAIService.executeSql(dto.getSql(), dto.getDatabaseName());
    }

    /**
     * sql优化
     */
    @PostMapping("/optimize-sql")
    public String optimizeSql(@RequestBody ExeDto dto){
        return sparkAIService.optimizeSql(dto.getSql(), dto.getDatabaseName());
    }

    /**
     * explain结果
     */
    @PostMapping("/explain-sql")
    public String explainSql(@RequestBody ExeDto dto){
        return sparkAIService.explainSql(dto.getSql(), dto.getDatabaseName());
    }

    /**
     * 结束连接
     * @return
     */
    @PostMapping("/terminate")
    public String terminateChat() {
        boolean terminated = sparkAIService.terminateChat();
        return terminated ? "Chat terminated successfully" : "No active chat to terminate";
    }
}