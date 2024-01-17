package cn.xiao.cg.model.dto.post;

import cn.hutool.core.util.ObjectUtil;
import cn.xiao.cg.model.entity.Post;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子 ES 包装类
 *
 * @author xiao
 **/
// TODO 取消注释开启 ES（须先配置 ES）
//@Document(indexName = "post")
@Data
public class PostEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";


    private static final Gson GSON = new Gson();

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 对象转包装类
     *
     * @param post
     * @return
     */
    public static PostEsDTO objToDto(Post post) {
        if (ObjectUtil.isEmpty(post)) {
            return null;
        }
        PostEsDTO postEsDTO = new PostEsDTO();
        BeanUtils.copyProperties(post, postEsDTO);
        String tagsStr = post.getTags();
        if (StringUtils.isNotBlank(tagsStr)) {
            postEsDTO.setTags(GSON.fromJson(tagsStr, new TypeToken<List<String>>() {
            }.getType()));
        }
        return postEsDTO;
    }

}
