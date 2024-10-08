package com.iteng.startup.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.iteng.startup.common.ResponseResult;
import com.iteng.startup.model.dto.demo.DemoAddDTO;
import com.iteng.startup.model.dto.demo.DemoPageDTO;
import com.iteng.startup.model.dto.demo.DemoUpdateDTO;
import com.iteng.startup.model.entity.Demo;
import com.iteng.startup.model.vo.DemoVO;
import com.iteng.startup.model.vo.PageVO;

import java.util.List;

/**
 * @author iteng
 * @date 2024-01-31 18:28
 */
public interface DemoService extends IService<Demo> {
    /**
     * 新增
     * @param dto
     * @return
     */
    ResponseResult<Void> add(DemoAddDTO dto);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    ResponseResult<Void> batchDelete(List<Long> ids);

    /**
     * 修改
     * @param dto
     * @return
     */
    ResponseResult<Void> updateInfo(DemoUpdateDTO dto);

    /**
     * 分页条件查询
     * @param dto
     * @return
     */
    ResponseResult<PageVO<DemoVO>> selectPage(DemoPageDTO dto);
}
