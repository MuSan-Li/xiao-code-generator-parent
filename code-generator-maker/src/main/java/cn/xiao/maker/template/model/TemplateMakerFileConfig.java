package cn.xiao.maker.template.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 模板文件配置
 *
 * @author xiao
 */
@Data
public class TemplateMakerFileConfig implements Serializable {

    private List<FileInfoConfig> fileInfoConfigList;

    private FileGroupConfig fileGroupConfig;

    @Data
    public static class FileInfoConfig implements Serializable {

        private String path;

        private List<FileFilterConfig> filterConfigList;
    }


    @Data
    public static class FileGroupConfig implements Serializable {
        private String condition;
        private String groupKey;
        private String groupName;
    }
}