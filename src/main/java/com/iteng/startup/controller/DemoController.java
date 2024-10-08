package com.iteng.startup.controller;

import com.iteng.startup.annotation.AuthCheck;
import com.iteng.startup.common.ErrorCode;
import com.iteng.startup.common.ResponseResult;
import com.iteng.startup.constant.CommonConstant;
import com.iteng.startup.constant.UserConstant;
import com.iteng.startup.exception.BusinessException;
import com.iteng.startup.model.dto.demo.DemoAddDTO;
import com.iteng.startup.model.dto.demo.DemoPageDTO;
import com.iteng.startup.model.dto.demo.DemoUpdateDTO;
import com.iteng.startup.model.property.ProjectProperty;
import com.iteng.startup.model.vo.DemoVO;
import com.iteng.startup.model.vo.PageVO;
import com.iteng.startup.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author iteng
 * @date 2024-01-31 18:27
 */
@RestController
@Slf4j
@RequestMapping("/demo")
public class DemoController extends CommonController{
    @Resource
    private DemoService demoService;

    @Resource
    private ProjectProperty property;

    /**
     * 新增
     * @param dto
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public ResponseResult<Void> add(@RequestBody DemoAddDTO dto){
        if (dto == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return demoService.add(dto);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @GetMapping("/batch_delete")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public ResponseResult<Void> batchDelete(@RequestParam("ids") List<Long> ids){
        if (ids == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return demoService.batchDelete(ids);
    }

    /**
     * 更新
     * @param dto
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(anyRole = {UserConstant.USER, UserConstant.ADMIN})
    public ResponseResult<Void> updateInfo(@RequestBody DemoUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return demoService.updateInfo(dto);
    }

    /**
     * 分页条件查询
     * @param dto
     * @return
     */
    @GetMapping("/page")
    @AuthCheck(anyRole = {UserConstant.USER, UserConstant.ADMIN})
    public ResponseResult<PageVO<DemoVO>> selectPage(DemoPageDTO dto){
        if (dto == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return demoService.selectPage(dto);
    }

    /**
     * 下载
     */
    @GetMapping("/download")
    @AuthCheck(anyRole = {UserConstant.USER, UserConstant.ADMIN})
    public void download(HttpServletResponse response) {
        String filePath = property.getPath() + CommonConstant.FILE_PATH;
        String fileName = "test.png";
        super.download(response, filePath, fileName);
    }

    /**
     * 上传
     */
    @PostMapping("/upload")
    @AuthCheck(anyRole = {UserConstant.USER, UserConstant.ADMIN})
    public ResponseResult<Void> upload(@RequestPart MultipartFile file){
        try {
            super.upload(property.getPath() + CommonConstant.FILE_PATH, "test.jpg", file.getInputStream());
        } catch (Exception e) {
            log.error("upload io异常", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "上传失败!");
        }
        return ResponseResult.success("上传成功！");
    }
}
