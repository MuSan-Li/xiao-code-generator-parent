package cn.xiao.maker.template.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 模板模型配置
 *
 * @author xiao
 */
@Data
public class TemplateMakerModelConfig implements Serializable {
    private List<ModelInfoConfig> models;
    private ModelGroupConfig modelGroupConfig;

    @Data
    public static class ModelInfoConfig implements Serializable {
        private String fieldName;
        private String type;
        private String description;
        private Object defaultValue;
        private String abbr;
        // 用于替换哪些文本
        private String replaceText;
    }

    @Data
    public static class ModelGroupConfig implements Serializable {
        private String condition;
        private String groupKey;
        private String groupName;
    }
}