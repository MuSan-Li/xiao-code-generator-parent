package cn.xiao.cg.model.vo;

import cn.hutool.json.JSONUtil;
import cn.xiao.cg.model.entity.Generator;
import cn.xiao.cg.model.meta.Meta;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 代码生成器表视图
 *
 * @author xiao
 */
@Data
public class GeneratorVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础包
     */
    private String basePackage;

    /**
     * 版本
     */
    private String version;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 图片
     */
    private String picture;

    /**
     * 文件配置（json字符串）
     */
    private Meta.FileConfigDTO fileConfig;

    /**
     * 模型配置（json字符串）
     */
    private Meta.ModelConfigDTO modelConfig;

    /**
     * 代码生成器产物路径
     */
    private String distPath;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 包装类转对象
     *
     * @param generatorVO
     * @return
     */
    public static Generator voToObj(GeneratorVO generatorVO) {
        if (Objects.isNull(generatorVO)) {
            return null;
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorVO, generator);
        generator.setTags(JSONUtil.toJsonStr(generatorVO.getTags()));
        generator.setFileConfig(JSONUtil.toJsonStr(generatorVO.getFileConfig()));
        generator.setModelConfig(JSONUtil.toJsonStr(generatorVO.getModelConfig()));
        return generator;
    }

    /**
     * 对象转包装类
     *
     * @param generator
     * @return
     */
    public static GeneratorVO objToVo(Generator generator) {
        if (Objects.isNull(generator)) {
            return new GeneratorVO();
        }
        GeneratorVO generatorVO = new GeneratorVO();
        BeanUtils.copyProperties(generator, generatorVO);
        generatorVO.setTags(JSONUtil.toList(generator.getTags(), String.class));
        generatorVO.setFileConfig(JSONUtil.toBean(generator.getFileConfig(), Meta.FileConfigDTO.class));
        generatorVO.setModelConfig(JSONUtil.toBean(generator.getModelConfig(), Meta.ModelConfigDTO.class));
        return generatorVO;
    }
}
