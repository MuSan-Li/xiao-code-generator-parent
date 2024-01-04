package cn.xiao.cg.model.meta;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 元信息配置
 *
 * @author xiao
 */
@Data
public class Meta implements Serializable {

    private String name;

    private String description;

    private String basePackage;

    private String version;

    private String author;

    private String createTime;

    private FileConfigDTO fileConfig;

    private ModelConfigDTO modelConfig;

    @Data
    public static class FileConfigDTO implements Serializable {

        private String sourceRootPath;

        private String inputRootPath;

        private String outputRootPath;

        private String type;

        private List<FilesDTO> files;


        @Data
        public static class FilesDTO implements Serializable {

            private String inputPath;

            private String outputPath;

            private String type;

            private String generateType;

            private String condition;

            //  groupKey：表示组的唯一标识
            private String groupKey;

            //  groupName：组的名称
            private String groupName;

            //  files：值为 group内的多个参数
            private List<FilesDTO> files;
        }
    }

    @Data
    public static class ModelConfigDTO implements Serializable {

        private List<ModelsDTO> models;

        @Data
        public static class ModelsDTO implements Serializable {

            private String fieldName;

            private String type;

            private String description;

            private Object defaultValue;

            private String abbr;

            private String groupKey;

            private String groupName;

            private List<ModelsDTO> models;

            private String condition;

            // 中间参数
            // 该分组下所有参数拼接字符串
            private String allArgsStr;
        }
    }
}
