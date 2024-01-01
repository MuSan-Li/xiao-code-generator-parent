package cn.xiao.springbootinit.model.dto.postthumb;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子点赞请求
 *
 * @author xiao
 */
@Data
public class PostThumbAddRequest implements Serializable {


    /**
     * 帖子 id
     */
    private Long postId;
}