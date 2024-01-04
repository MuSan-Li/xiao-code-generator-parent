package cn.xiao.cg.aop;

import cn.hutool.core.util.ObjectUtil;
import cn.xiao.cg.annotation.AuthCheck;
import cn.xiao.cg.common.ErrorCode;
import cn.xiao.cg.enums.UserRoleEnum;
import cn.xiao.cg.exception.BusinessException;
import cn.xiao.cg.model.entity.User;
import cn.xiao.cg.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 权限校验 AOP
 *
 * @author xiao
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 必须有该权限才通过
        if (ObjectUtil.isEmpty(mustRole)) {
            return joinPoint.proceed();
        }
        UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        if (ObjectUtil.isEmpty(mustUserRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        String userRole = loginUser.getUserRole();
        // 如果被封号，直接拒绝
        if (Objects.equals(UserRoleEnum.BAN, mustUserRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 必须有管理员权限
        if (Objects.equals(UserRoleEnum.ADMIN, mustUserRoleEnum)) {
            if (ObjectUtil.equals(mustRole, userRole)) {
                return joinPoint.proceed();
            }
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

