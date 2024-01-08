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

    /**
     * 是否替换包名 默认 false 如果为 true 替换文件夹路径
     */
    private boolean isBasePackage = false;

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

        private String type;

        private String description;
    }
}