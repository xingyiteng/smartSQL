package com.iteng.startup.service.impl;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iteng.startup.common.ErrorCode;
import com.iteng.startup.common.ResponseResult;
import com.iteng.startup.constant.RedisConstant;
import com.iteng.startup.constant.UserConstant;
import com.iteng.startup.exception.BusinessException;
import com.iteng.startup.mapper.UserMapper;
import com.iteng.startup.model.dto.*;
import com.iteng.startup.model.dto.user.*;
import com.iteng.startup.model.entity.User;
import com.iteng.startup.model.enums.UserEnum;
import com.iteng.startup.model.property.EmailProperty;
import com.iteng.startup.model.vo.CaptchaVO;
import com.iteng.startup.model.vo.PageVO;
import com.iteng.startup.model.vo.UserVO;
import com.iteng.startup.service.UserService;
import com.iteng.startup.utils.BeanCopyUtil;
import com.iteng.startup.utils.CacheUtils;
import com.iteng.startup.utils.CaptchaUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.iteng.startup.constant.RedisConstant.STARTUP_LOGIN_CAPTCHA_ID_KEY;

/**
 * @author iteng
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-12-30 15:42:50
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private EmailProperty emailProperty;

    @Override
    public ResponseResult<CaptchaVO> getCaptcha() {
        // 1. 生成验证码和图片流
        TextAndBufferedImgDTO captchaObject = CaptchaUtils.createCaptchaWithMath();
        String text = captchaObject.getText();
        BufferedImage bufferedImage = captchaObject.getBufferedImage();

        String captchaId = IdUtil.simpleUUID();
        String key = STARTUP_LOGIN_CAPTCHA_ID_KEY + captchaId;

        // 2. 将验证码缓存到redis中 过期时间：120s
        CacheUtils.redisCacheSet(key, text, RedisConstant.STARTUP_LOGIN_CAPTCHA_ID_TTL, TimeUnit.SECONDS);

        // 3. 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", os);
        } catch (IOException e) {
            log.error("getCaptcha() error", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        // 4. 将Base64加密后的图片流, captchaId封装后返回给前端
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaId(captchaId);
        captchaVO.setBase64Image(Base64Encoder.encode(os.toByteArray()));
        return ResponseResult.success(captchaVO);
    }

    @Override
    public ResponseResult<Void> register(UserRegisterDTO dto) {
        // 参数校验
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = dto.getUserAccount();
        String userPassword = dto.getUserPassword();
        String rePassword = dto.getRePassword();

        // account不为null	 >= 4位 && <=30位
        if (StringUtils.isBlank(userAccount) || userAccount.length() < UserConstant.USER_ACCOUNT_MIN_LENGTH || userAccount.length() > UserConstant.ACCOUNT_MAX_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "账号长度要求4-30位，请重新输入！");
        }

        // password不为null   >= 6位
        if (StringUtils.isBlank(userPassword) || userPassword.length() < UserConstant.USER_PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "密码至少6位，请重新输入！");
        }

        // rePassword不为null  && 与password相同
        if (StringUtils.isBlank(rePassword) || !Objects.equals(userPassword, rePassword)) {
            return ResponseResult.error(ErrorCode.PARAMS_FORMAT_ERROR, "确认密码不正确，请重新输入！");
        }

        // 校验account是否已存在
        User userDb = lambdaQuery().eq(User::getUserAccount, userAccount).one();

        // 已存在：直接返回失败
        if (userDb != null) {
            return ResponseResult.error(ErrorCode.DATA_FOUND_ERROR, "账号已存在，请重新输入！");
        }

        // 不存在：数据库添加用户
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(DigestUtils.md5DigestAsHex((UserConstant.USER_SALT + userPassword).getBytes()));
        user.setUsername(RandomStringUtils.random(UserConstant.DEFAULT_USERNAME_LENGTH, true, true));
        boolean result = save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败！");
        }
        return ResponseResult.success("注册成功！");
    }

    @Override
    public ResponseResult<UserVO> login(UserLoginDTO dto) {
        // 参数校验
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = dto.getUserAccount();
        String userPassword = dto.getUserPassword();
        String captchaText = dto.getCaptchaText();
        String captchaId = dto.getCaptchaId();

        // account不为null	 >= 4位 && <=30位
        if (StringUtils.isBlank(userAccount) || userAccount.length() < UserConstant.USER_ACCOUNT_MIN_LENGTH || userAccount.length() > UserConstant.ACCOUNT_MAX_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "账号有误，请重新输入！");
        }

        // password不为null   >= 6位
        if (StringUtils.isBlank(userPassword) || userPassword.length() < UserConstant.USER_PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "密码有误，请重新输入！");
        }

        // captchaText不为null
        if (StringUtils.isBlank(captchaText)) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "验证码错误，请重新输入！");
        }

        // captchaId不为null
        if (StringUtils.isBlank(captchaId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 根据captchaId在redis中查询
        String key = STARTUP_LOGIN_CAPTCHA_ID_KEY + captchaId;

        // 从redis中查询验证码
        String text = (String) CacheUtils.redisCacheGet(key);

        // 验证码不存在或不相等，返回失败
        if (StringUtils.isBlank(text) || !Objects.equals(text, captchaText)) {
            return ResponseResult.error(ErrorCode.NOT_FOUND_ERROR, "验证码错误，请重新输入！");
        }

        // 校验account是否存在
        User userDb = lambdaQuery().eq(User::getUserAccount, userAccount).eq(User::getUserPassword, DigestUtils.md5DigestAsHex((UserConstant.USER_SALT + userPassword).getBytes())).one();

        // 不存在：账号或密码错误
        if (userDb == null) {
            return ResponseResult.error(ErrorCode.NOT_FOUND_ERROR, "账号或密码错误！");
        }

        // 存在，是否被禁用
        Integer userStatus = userDb.getUserStatus();
        if (Objects.equals(userStatus, UserEnum.DISABLE.getValue())){
            return ResponseResult.error(ErrorCode.FORBIDDEN_ERROR, "账户已被禁用，请联系管理员！");
        }

        // 保存登录态，将userId保存到session中, 过期时间：1天
        CacheUtils.sessionCacheSet(UserConstant.LOGIN_USER_ID, userDb.getUserId(), UserConstant.USER_ID_EXPIRE);

        // 清除验证码缓存
        CacheUtils.redisCacheClear(key);

        UserVO userVO = BeanCopyUtil.copyProperties(userDb, UserVO.class);
        return ResponseResult.success(userVO, "登录成功！");
    }

    @Override
    public ResponseResult<PageVO<UserVO>> userPage(UserPageDTO dto) {
        // 校验参数
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = dto.getUserAccount();
        String username = dto.getUsername();
        Integer gender = dto.getGender();
        String phone = dto.getPhone();
        Integer userStatus = dto.getUserStatus();

        long pageSize = dto.getPageSize();
        long pageNum = dto.getPageNum();

        if (pageSize <= 0 || pageNum <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 拼接查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(userAccount), User::getUserAccount, userAccount);
        wrapper.like(StringUtils.isNotBlank(username), User::getUsername, username);
        wrapper.eq(gender != null, User::getGender, gender);
        wrapper.eq(StringUtils.isNotBlank(phone), User::getPhone, phone);
        wrapper.eq(userStatus != null, User::getUserStatus, userStatus);

        // 分页参数
        IPage<User> page = new Page<>(pageNum, pageSize);
        page = page(page, wrapper);
        List<User> records = page.getRecords();
        long total = page.getTotal();
        long size = page.getSize();
        long current = page.getCurrent();

        // 封装分页返回结果
        PageVO<UserVO> userPageVO = new PageVO<>();
        userPageVO.setPageNum(current);
        userPageVO.setPageSize(size);
        userPageVO.setTotal(total);

        // 拷贝List<User> => List<UserVO>
        List<UserVO> list = BeanCopyUtil.copyWithCollection(records, UserVO.class);
        userPageVO.setRecords(list);

        return ResponseResult.success(userPageVO);
    }

    @Override
    public ResponseResult addUserByAdmin(UserAddDTO dto) {

        // 校验参数
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = dto.getUserAccount();
        Integer userRole = dto.getUserRole();

        // account不为null	 >= 4位 && <=30位
        if (StringUtils.isBlank(userAccount) || userAccount.length() < UserConstant.USER_ACCOUNT_MIN_LENGTH || userAccount.length() > UserConstant.ACCOUNT_MAX_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "账号长度要求4-30位，请重新输入！");
        }

        // userRole不为null 只能是 0 / 1
        if (userRole == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验account是否已存在
        Long count = lambdaQuery().eq(User::getUserAccount, userAccount).count();

        // 已存在：直接返回失败
        if (count > 0) {
            return ResponseResult.error(ErrorCode.DATA_FOUND_ERROR, "账号已存在，请重新输入！");
        }

        // 不存在：数据库添加用户
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(DigestUtils.md5DigestAsHex(UserConstant.DEFAULT_USER_PASSWORD.getBytes()));
        user.setUsername(RandomStringUtils.random(UserConstant.DEFAULT_USERNAME_LENGTH, true, true));
        user.setUserRole(userRole);
        boolean result = save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加失败！");
        }
        return ResponseResult.success("添加成功！");
    }

    @Override
    public ResponseResult<Void> batchDeleteUser(List<Long> list) {
        if (list == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 逻辑删除
        boolean result = removeByIds(list);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败！");
        }
        return ResponseResult.success( "删除成功！");
    }

    @Override
    public ResponseResult<Void> updateUserByAdmin(UserUpdateByAdminDTO dto) {

        // 校验参数
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String username = dto.getUsername();
        Integer gender = dto.getGender();
        String phone = dto.getPhone();
        Integer userStatus = dto.getUserStatus();
        Long userId = dto.getUserId();

        if (StringUtils.isBlank(username)) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "姓名格式输入有误！");
        }

        if (gender != null && (gender < 0 || gender > 2) ) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "性别输入有误！");
        }

        if (userStatus == null || (!userStatus.equals(UserEnum.ACTIVATE.getValue()) && !userStatus.equals(UserEnum.DISABLE.getValue()))){
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "用户状态输入有误！");
        }

        User updateUser = new User();
        updateUser.setUserId(userId);
        updateUser.setUsername(username);
        updateUser.setGender(gender);
        updateUser.setPhone(phone);
        updateUser.setUserStatus(userStatus);

        boolean result = updateById(updateUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败！");
        }
        return ResponseResult.success("修改成功！");
    }

    @Override
    public ResponseResult logout() {
        // 清除session
        CacheUtils.sessionCacheClear(UserConstant.LOGIN_USER_ID);
        return ResponseResult.success( "退出成功！");
    }

    @Override
    public ResponseResult<Void> getEmailCaptcha(String userAccount) {

        // 校验邮箱格式
        boolean res = Validator.isEmail(userAccount);
        if (!res) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "邮箱格式错误！");
        }

        // 生成邮箱验证码
        String captchaText = RandomStringUtils.random(UserConstant.EMAIL_CAPTCHA_LENGTH, false, true);

        // 保存到redis
        CacheUtils.redisCacheSet(RedisConstant.STARTUP_EMAIL_CAPTCHA_ACCOUNT_KEY + userAccount, captchaText, RedisConstant.STARTUP_REGISTER_CAPTCHA_ACCOUNT_TTL, TimeUnit.SECONDS);

        // 发送邮件到userAccount
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailProperty.getUsername()+"(vue-startup)");
        message.setTo(userAccount);
        message.setSubject("邮箱验证码");
        message.setText("验证码：" + captchaText + "，有效时间：2分钟。");
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "邮箱错误！");
        }

        return ResponseResult.success("验证码已发送至邮箱！");
    }

    @Override
    public ResponseResult<Void> emailRegister(UserEmailRegisterDTO dto) {

        if (dto == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = dto.getUserAccount();
        String userPassword = dto.getUserPassword();
        String rePassword = dto.getRePassword();
        String captchaText = dto.getCaptchaText();

        if (StringUtils.isBlank(captchaText)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 从redis中获取captchaText
        String cacheCaptchaText = (String)CacheUtils.redisCacheGet(RedisConstant.STARTUP_EMAIL_CAPTCHA_ACCOUNT_KEY + userAccount);
        if (!captchaText.equals(cacheCaptchaText)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "验证码错误！");
        }

        // password不为null   >= 6位
        if (StringUtils.isBlank(userPassword) || userPassword.length() < UserConstant.USER_PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "密码至少6位，请重新输入！");
        }

        // rePassword不为null  && 与password相同
        if (StringUtils.isBlank(rePassword) || !Objects.equals(userPassword, rePassword)) {
            return ResponseResult.error(ErrorCode.PARAMS_FORMAT_ERROR, "确认密码不正确，请重新输入！");
        }

        // 校验userAccount是否已存在
        Long count = lambdaQuery().eq(User::getUserAccount, userAccount).count();
        if (count == null || count > 0) {
            throw new BusinessException(ErrorCode.DATA_FOUND_ERROR, "邮箱已存在！");
        }

        // 数据库添加用户
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(DigestUtils.md5DigestAsHex((UserConstant.USER_SALT + userPassword).getBytes()));
        user.setUsername(RandomStringUtils.random(UserConstant.DEFAULT_USERNAME_LENGTH, true, true));
        boolean result = save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败！");
        }

        // 清除redis缓存
        CacheUtils.redisCacheClear(RedisConstant.STARTUP_EMAIL_CAPTCHA_ACCOUNT_KEY + userAccount);
        return ResponseResult.success("注册成功！");
    }

    @Override
    public ResponseResult<UserVO> getUserInfo() {
        User loginUser = getLoginUser();
        UserVO userVO = BeanCopyUtil.copyProperties(loginUser, UserVO.class);
        return ResponseResult.success(userVO);
    }

    @Override
    public ResponseResult<Void> updatePassword(UserUpdatePasswordDTO dto) {

        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = getLoginUser().getUserId();
        String userAccount = dto.getUserAccount();
        String userPassword = dto.getUserPassword();
        String rePassword = dto.getRePassword();
        String captchaText = dto.getCaptchaText();

        if (StringUtils.isBlank(captchaText)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String cacheCaptchaText = (String)CacheUtils.redisCacheGet(RedisConstant.STARTUP_EMAIL_CAPTCHA_ACCOUNT_KEY + userAccount);

        if (!captchaText.equals(cacheCaptchaText)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "验证码错误！");
        }

        if (StringUtils.isBlank(userPassword) || userPassword.length() < UserConstant.USER_PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "密码至少6位，请重新输入！");
        }

        if (StringUtils.isBlank(rePassword) || !Objects.equals(userPassword, rePassword)) {
            return ResponseResult.error(ErrorCode.PARAMS_FORMAT_ERROR, "确认密码不正确，请重新输入！");
        }

        User updateUser = new User();
        updateUser.setUserId(userId);
        updateUser.setUserPassword(DigestUtils.md5DigestAsHex((UserConstant.USER_SALT + userPassword).getBytes()));

        updateById(updateUser);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult<Void> updateUser(UserUpdateDTO dto) {

        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = getLoginUser().getUserId();
        String username = dto.getUsername();
        String phone = dto.getPhone();
        Integer gender = dto.getGender();

        User updateUser = new User();
        updateUser.setUserId(userId);
        updateUser.setUsername(username);
        updateUser.setPhone(phone);
        updateUser.setGender(gender);

        updateById(updateUser);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult<Void> forgotPassword(UserEmailRegisterDTO dto) {

        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = dto.getUserAccount();
        String userPassword = dto.getUserPassword();
        String rePassword = dto.getRePassword();
        String captchaText = dto.getCaptchaText();

        if (StringUtils.isBlank(captchaText)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 从redis中获取captchaText
        String cacheCaptchaText = (String)CacheUtils.redisCacheGet(RedisConstant.STARTUP_EMAIL_CAPTCHA_ACCOUNT_KEY + userAccount);
        if (!captchaText.equals(cacheCaptchaText)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "验证码错误！");
        }

        // password不为null   >= 6位
        if (StringUtils.isBlank(userPassword) || userPassword.length() < UserConstant.USER_PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, "密码至少6位，请重新输入！");
        }

        // rePassword不为null  && 与password相同
        if (StringUtils.isBlank(rePassword) || !Objects.equals(userPassword, rePassword)) {
            return ResponseResult.error(ErrorCode.PARAMS_FORMAT_ERROR, "确认密码不正确，请重新输入！");
        }

        // 校验userAccount是否已存在
        User userDb = lambdaQuery().eq(User::getUserAccount, userAccount).one();
        if (userDb != null) {
            // 已存在，修改用户密码
            User updateUser = new User();
            updateUser.setUserId(userDb.getUserId());
            updateUser.setUserPassword(DigestUtils.md5DigestAsHex((UserConstant.USER_SALT + userPassword).getBytes()));
            updateById(updateUser);
            return ResponseResult.success();
        }

        // 不存在，数据库添加用户
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(DigestUtils.md5DigestAsHex((UserConstant.USER_SALT + userPassword).getBytes()));
        user.setUsername(RandomStringUtils.random(UserConstant.DEFAULT_USERNAME_LENGTH, true, true));
        boolean result = save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "找回失败！");
        }

        // 清除redis缓存
        CacheUtils.redisCacheClear(RedisConstant.STARTUP_EMAIL_CAPTCHA_ACCOUNT_KEY + userAccount);
        return ResponseResult.success();
    }

    @Override
    public User getLoginUser() {
        // 从session中获取userId
        Long userId = (Long) CacheUtils.sessionCacheGet(UserConstant.LOGIN_USER_ID);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User user = lambdaQuery().eq(User::getUserStatus, UserEnum.ACTIVATE.getValue()).eq(User::getUserId, userId).one();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }
}




