package cn.xiao.cg.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.xiao.cg.common.ErrorCode;
import cn.xiao.cg.constant.UserConstant;
import cn.xiao.cg.enums.UserRoleEnum;
import cn.xiao.cg.exception.BusinessException;
import cn.xiao.cg.mapper.UserMapper;
import cn.xiao.cg.model.dto.user.UserAddRequest;
import cn.xiao.cg.model.dto.user.UserQueryRequest;
import cn.xiao.cg.model.entity.User;
import cn.xiao.cg.model.vo.LoginUserVO;
import cn.xiao.cg.model.vo.UserVO;
import cn.xiao.cg.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 * @author xiao
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "xiao";

    /**
     * 校验用户信息
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     */
    private static void checkRegisterUserInfo(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }

        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }

        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        checkRegisterUserInfo(userAccount, userPassword, checkPassword);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(userPassword);
        // 默认用户名
        String userName = "user_" + RandomUtil.randomNumbers(4);
        user.setUserName(userName);
        return userRegister(user);
    }

    @Override
    public long userRegister(User user) {
        String userAccount = user.getUserAccount();
        String userPassword = user.getUserPassword();
        synchronized (userAccount.intern()) {
            // 账户不能重复
            LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(User::getUserAccount, userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User userRegister = new User();
            userRegister.setUserAccount(userAccount);
            userRegister.setUserPassword(encryptPassword);
            userRegister.setUserName(user.getUserName());
            userRegister.setUserAvatar(user.getUserAvatar());
            userRegister.setUserProfile(user.getUserProfile());
            userRegister.setUserRole(user.getUserRole());
            boolean saveResult = this.save(userRegister);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return userRegister.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(User::getUserAccount, userAccount);
        queryWrapper.eq(User::getUserPassword, encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (ObjectUtils.isEmpty(user)) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (ObjectUtils.isEmpty(currentUser) || ObjectUtils.isEmpty(currentUser.getId())) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (ObjectUtils.isEmpty(currentUser)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return ObjectUtils.isNotEmpty(user) && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (ObjectUtils.isEmpty(attribute)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (ObjectUtils.isEmpty(user)) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (ObjectUtils.isEmpty(user)) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (ObjectUtils.isEmpty(userQueryRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userName = userQueryRequest.getUserName();
        String userRole = userQueryRequest.getUserRole();
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), User::getId, id);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), User::getUserRole, userRole);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), User::getUserRole, userRole);
        queryWrapper.eq(StringUtils.isNotBlank(userAccount), User::getUserAccount, userAccount);
        queryWrapper.like(StringUtils.isNotBlank(userName), User::getUserName, userName);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), User::getUserProfile, userProfile);
        return queryWrapper;
    }

    @Override
    public boolean addUser(UserAddRequest addRequest) {

        if (Objects.isNull(addRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userName = addRequest.getUserName();
        // TODO userAccount 生成可能会有冲突
        String userAccount = RandomUtil.randomString(10);
        String userAvatar = addRequest.getUserAvatar();
        String userRole = addRequest.getUserRole();
        String userProfile = addRequest.getUserProfile();
        if (CharSequenceUtil.hasBlank(userAccount, userAvatar, userRole)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        String userPassword = "12345678";
        checkRegisterUserInfo(userAccount, userPassword, userPassword);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(userPassword);
        user.setUserName(userName);
        user.setUserAvatar(userAvatar);
        user.setUserProfile(userProfile);
        user.setUserRole(userRole);
        long userId = userRegister(user);
        log.info("userId:{}", userId);
        return true;
    }
}
