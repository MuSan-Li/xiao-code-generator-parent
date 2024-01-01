package cn.xiao.springbootinit.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.xiao.springbootinit.common.BaseResponse;
import cn.xiao.springbootinit.common.ErrorCode;
import cn.xiao.springbootinit.common.ResultUtils;
import cn.xiao.springbootinit.exception.BusinessException;
import cn.xiao.springbootinit.model.dto.postthumb.PostThumbAddRequest;
import cn.xiao.springbootinit.model.entity.User;
import cn.xiao.springbootinit.service.PostThumbService;
import cn.xiao.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞接口
 *
 * @author xiao
 */
@RestController
@RequestMapping("/post_thumb")
@Slf4j
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
                                         HttpServletRequest request) {
        if (ObjectUtil.isEmpty(postThumbAddRequest) || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ResultUtils.success(result);
    }

}
