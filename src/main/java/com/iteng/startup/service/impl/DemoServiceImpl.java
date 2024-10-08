package com.iteng.startup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iteng.startup.common.ErrorCode;
import com.iteng.startup.common.ResponseResult;
import com.iteng.startup.exception.BusinessException;
import com.iteng.startup.mapper.DemoMapper;
import com.iteng.startup.model.dto.demo.DemoAddDTO;
import com.iteng.startup.model.dto.demo.DemoPageDTO;
import com.iteng.startup.model.dto.demo.DemoUpdateDTO;
import com.iteng.startup.model.entity.Demo;
import com.iteng.startup.model.entity.User;
import com.iteng.startup.model.vo.DemoVO;
import com.iteng.startup.model.vo.PageVO;
import com.iteng.startup.service.DemoService;
import com.iteng.startup.service.UserService;
import com.iteng.startup.utils.BeanCopyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author iteng
 * @date 2024-01-31 18:34
 */
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper,Demo> implements DemoService {

    @Resource
    private UserService userService;

    @Override
    public ResponseResult<Void> add(DemoAddDTO dto) {
        // 参数校验
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        User loginUser = userService.getLoginUser();

        // 参数校验

        // 设置信息
        Demo demo = BeanCopyUtil.copyProperties(dto, Demo.class);
        demo.setUserId(loginUser.getUserId());

        // 保存用户
        boolean result = save(demo);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加失败！");
        }
        return ResponseResult.success("添加成功！");
    }

    @Override
    public ResponseResult<Void> batchDelete(List<Long> ids) {
        // 参数校验
        if (ids == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 逻辑删除
        boolean result = removeByIds(ids);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败！");
        }
        return ResponseResult.success( "删除成功！");
    }

    @Override
    public ResponseResult<Void> updateInfo(DemoUpdateDTO dto) {
        // 校验参数
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        User loginUser = userService.getLoginUser();

        // 参数校验

        // 更新信息  null值不更新
        Demo updateDemo = BeanCopyUtil.copyProperties(dto, Demo.class);
        updateDemo.setUserId(loginUser.getUserId());

        boolean result = updateById(updateDemo);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败！");
        }
        return ResponseResult.success("修改成功！");
    }

    @Override
    public ResponseResult<PageVO<DemoVO>> selectPage(DemoPageDTO dto) {
        // 校验参数
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long pageNum = dto.getPageNum();
        long pageSize = dto.getPageSize();
        String username = dto.getUsername();

        // 校验参数
        if (pageSize <= 0 || pageNum <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 拼接查询条件
        LambdaQueryWrapper<Demo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(username), Demo::getUsername, username);

        // 分页参数
        IPage<Demo> page = new Page<>(pageNum, pageSize);
        page = page(page, wrapper);
        List<Demo> records = page.getRecords();
        long total = page.getTotal();
        long size = page.getSize();
        long current = page.getCurrent();

        // 封装分页返回结果
        PageVO<DemoVO> DemoVO = new PageVO<>();
        DemoVO.setPageNum(current);
        DemoVO.setPageSize(size);
        DemoVO.setTotal(total);

        // 拷贝List<Demo> => List<DemoVo>
        List<DemoVO> list = BeanCopyUtil.copyWithCollection(records, DemoVO.class);
        DemoVO.setRecords(list);

        return ResponseResult.success(DemoVO);
    }
}
