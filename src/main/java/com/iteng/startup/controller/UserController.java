package com.iteng.startup.controller;

import com.iteng.startup.annotation.AuthCheck;
import com.iteng.startup.common.ErrorCode;
import com.iteng.startup.common.ResponseResult;
import com.iteng.startup.constant.UserConstant;
import com.iteng.startup.exception.BusinessException;
import com.iteng.startup.model.dto.user.*;
import com.iteng.startup.model.vo.CaptchaVO;
import com.iteng.startup.model.vo.PageVO;
import com.iteng.startup.model.vo.UserVO;
import com.iteng.startup.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author iteng
 * @date 2023-12-30 15:56
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 生成验证码
     * @return
     */
    @GetMapping("/captcha")
    public ResponseResult<CaptchaVO> getCaptcha() {
        return userService.getCaptcha();
    }

    /**
     * 生成邮箱验证码
     * @return
     */
    @GetMapping("/email_captcha")
    public ResponseResult<Void> getEmailCaptcha(@RequestParam String userAccount){
        if (StringUtils.isBlank(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.getEmailCaptcha(userAccount);
    }

    /**
     * 用户登录
     * @param dto
     * @return
     */
    @PostMapping("/login")
    public ResponseResult<UserVO> login(@RequestBody UserLoginDTO dto) {
        if (dto == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.login(dto);
    }

    /**
     * 用户注册
     * @param dto
     * @return
     */
    @PostMapping("/register")
    public ResponseResult<Void> register(@RequestBody UserRegisterDTO dto){
        if (dto == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.register(dto);
    }

    /**
     * 用户邮箱注册
     * @param dto
     * @return
     */
    @PostMapping("/email_register")
    public ResponseResult<Void> emailRegister(@RequestBody UserEmailRegisterDTO dto){
        if (dto == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.emailRegister(dto);
    }

    /**
     * 批量删除用户
     * @param ids
     * @return
     */
    @PostMapping("/batch_delete")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public ResponseResult<Void> batchDeleteUser(@RequestParam("ids") List<Long> ids){
        if (ids == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.batchDeleteUser(ids);
    }

    /**
     * 条件分页
     * @param dto
     * @return
     */
    @GetMapping("/page")
    @AuthCheck(anyRole = {UserConstant.ADMIN})
    public ResponseResult<PageVO<UserVO>> userPage(UserPageDTO dto){
        if (dto == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.userPage(dto);
    }

    /**
     * 管理员添加用户
     * @param dto
     * @return
     */
    @PostMapping("/admin_add")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public ResponseResult<Void> addUser(@RequestBody UserAddDTO dto){
        if (dto == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.addUserByAdmin(dto);
    }

    /**
     * 管理员修改用户
     * @param dto
     * @return
     */
    @PostMapping("/admin_update")
    @AuthCheck(anyRole = {UserConstant.ADMIN})
    public ResponseResult<Void> updateUserByAdmin(@RequestBody UserUpdateByAdminDTO dto) {
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.updateUserByAdmin(dto);
    }

    /**
     * 更新用户信息
     * @param dto
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(anyRole = {UserConstant.USER, UserConstant.ADMIN})
    public ResponseResult<Void> updateUser(@RequestBody UserUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.updateUser(dto);
    }

    /**
     * 修改密码
     * @param dto
     * @return
     */
    @PostMapping("/update_password")
    @AuthCheck(anyRole = {UserConstant.USER, UserConstant.ADMIN})
    public ResponseResult<Void> updatePassword(@RequestBody UserUpdatePasswordDTO dto) {
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.updatePassword(dto);
    }

    /**
     * 用户退出
     * @return
     */
    @GetMapping("/logout")
    @AuthCheck(anyRole = {UserConstant.USER, UserConstant.ADMIN})
    public ResponseResult<Void> logout() {
        return userService.logout();
    }

    /**
     * 获取登录的用户信息
     * @return
     */
    @GetMapping("/user_info")
    public ResponseResult<UserVO> getUserInfo(){
        return userService.getUserInfo();
    }


    /**
     * 找回密码
     * @param dto
     * @return
     */
    @PostMapping("/forgot_password")
    public ResponseResult<Void> forgotPassword(@RequestBody UserEmailRegisterDTO dto) {
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.forgotPassword(dto);
    }
}
