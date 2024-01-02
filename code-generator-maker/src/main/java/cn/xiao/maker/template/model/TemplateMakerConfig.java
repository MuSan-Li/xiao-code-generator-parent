package cn.xiao.maker.template.model;

import cn.xiao.maker.meta.Meta;
import lombok.Data;

import java.io.Serializable;

/**
 * 模板制作配置
 *
 * @author xiao
 */
@Data
public class TemplateMakerConfig implements Serializable {

    private Long id;

    private Meta meta = new Meta();

    private String originProjectPath;

    TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

}