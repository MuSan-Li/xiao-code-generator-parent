package cn.xiao.cg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 代码生成器信息表
 *
 * @author xiao
 */
@Data
@TableName(value = "t_generator")
public class Generator implements Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 基础包
     */
    @TableField(value = "base_package")
    private String basePackage;

    /**
     * 版本
     */
    @TableField(value = "version")
    private String version;

    /**
     * 作者
     */
    @TableField(value = "author")
    private String author;

    /**
     * 标签列表（json 数组）
     */
    @TableField(value = "tags")
    private String tags;

    /**
     * 图片
     */
    @TableField(value = "picture")
    private String picture;

    /**
     * 文件配置（json字符串）
     */
    @TableField(value = "file_config")
    private String fileConfig;

    /**
     * 模型配置（json字符串）
     */
    @TableField(value = "model_config")
    private String modelConfig;

    /**
     * 代码生成器产物路径
     */
    @TableField(value = "dist_path")
    private String distPath;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;

}