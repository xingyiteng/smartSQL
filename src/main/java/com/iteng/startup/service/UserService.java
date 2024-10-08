package com.iteng.startup.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iteng.startup.common.ResponseResult;
import com.iteng.startup.model.dto.user.*;
import com.iteng.startup.model.entity.User;
import com.iteng.startup.model.vo.CaptchaVO;
import com.iteng.startup.model.vo.PageVO;
import com.iteng.startup.model.vo.UserVO;

import java.util.List;

/**
* @author iteng
* @description 针对表【user】的数据库操作Service
* @createDate 2023-12-30 15:42:50
*/
public interface UserService extends IService<User> {
    /**
     * 生成登录验证码
     * @return
     */
    ResponseResult<CaptchaVO> getCaptcha();

    /**
     * 用户注册
     * @param dto
     * @return
     */
    ResponseResult<Void> register(UserRegisterDTO dto);

    /**
     * 用户登录
     * @param dto
     * @return
     */
    ResponseResult<UserVO> login(UserLoginDTO dto);

    /**
     * 条件分页
     * @param dto
     * @return
     */
    ResponseResult<PageVO<UserVO>> userPage(UserPageDTO dto);

    /**
     * 管理员添加用户
     * @param dto
     * @return
     */
    ResponseResult<Void> addUserByAdmin(UserAddDTO dto);

    /**
     * 批量删除用户
     * @param list
     * @return
     */
    ResponseResult<Void> batchDeleteUser(List<Long> list);

    /**
     * 获取当前登录的用户信息
     * @return
     */
    User getLoginUser();

    /**
     * 管理员修改用户信息
     * @param dto
     * @return
     */
    ResponseResult<Void> updateUserByAdmin(UserUpdateByAdminDTO dto);


    /**
     * 用户退出
     * @return
     */
    ResponseResult<Void> logout();

    /**
     * 生成邮箱验证码
     * @return
     */
    ResponseResult<Void> getEmailCaptcha(String userAccount);

    /**
     * 用户邮箱注册
     * @param dto
     * @return
     */
    ResponseResult<Void> emailRegister(UserEmailRegisterDTO dto);

    /**
     * 获取登录的用户信息
     * @return
     */
    ResponseResult<UserVO> getUserInfo();

    /**
     * 修改密码
     * @param dto
     * @return
     */
    ResponseResult<Void> updatePassword(UserUpdatePasswordDTO dto);

    /**
     * 更新用户信息
     * @param dto
     * @return
     */
    ResponseResult<Void> updateUser(UserUpdateDTO dto);

    /**
     * 找回密码
     * @param dto
     * @return
     */
    ResponseResult<Void> forgotPassword(UserEmailRegisterDTO dto);
}
