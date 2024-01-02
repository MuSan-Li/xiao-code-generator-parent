package cn.xiao.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.xiao.maker.template.enums.FileFilterRangeEnum;
import cn.xiao.maker.template.enums.FileFilterRuleEnum;
import cn.xiao.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 模板文件过滤器
 *
 * @author xiao
 */
public class TemplateFileFilter {

    private TemplateFileFilter() {
    }

    /**
     * 对某个文件或目录进行过滤返回文件列表
     *
     * @param filePath
     * @param fileFilterConfigList
     * @return
     */
    public static List<File> doFilter(String filePath, List<FileFilterConfig> fileFilterConfigList) {
        List<File> fileList = FileUtil.loopFiles(filePath);
        return fileList.stream()
                .filter(item -> doSingleFilter(fileFilterConfigList, item))
                .collect(Collectors.toList());
    }


    /**
     * This method takes in a list of FileFilterConfig objects and a File object and returns a boolean
     * 按照匹配规则检查单个文件名称或内容进行过滤
     *
     * @param filterConfigList
     * @param file
     * @return
     */
    public static boolean doSingleFilter(List<FileFilterConfig> filterConfigList, File file) {
        // Check if the file exists
        // if (!FileUtil.exist(file)) {
        //     return false;
        // }
        // Get the file name and content
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        // Check if the filter configuration list is empty
        if (CollUtil.isEmpty(filterConfigList)) {
            return true;
        }
        // Iterate through the filter configuration list
        for (FileFilterConfig fileFilterConfig : filterConfigList) {
            // Get the range, rule, and value from the filter configuration
            String range = fileFilterConfig.getRange();
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();
            // Check if the range, rule, and value are empty
            if (CharSequenceUtil.hasBlank(range, rule, value)) {
                continue;
            }
            // Get the filter range enum and filter rule enum from the range and rule
            FileFilterRangeEnum filterRangeEnum = FileFilterRangeEnum.getEnumByValue(range);
            FileFilterRuleEnum filterRuleEnum = FileFilterRuleEnum.getEnumByValue(rule);

            // Get the match text
            String matchText = fileName;
            // Check if the filter range enum is file content
            if (Objects.equals(FileFilterRangeEnum.FILE_CONTENT, filterRangeEnum)) {
                matchText = fileContent;
            }
            // Check if the filter rule enum is null
            if (Objects.isNull(filterRuleEnum)) {
                continue;
            }
            // Initialize the result
            boolean result = true;
            // Check the result based on the filter rule enum
            switch (filterRuleEnum) {
                case REGEX:
                    result = matchText.matches(value);
                    break;
                case EQUALS:
                    result = matchText.equals(value);
                    break;
                case CONTAINS:
                    result = matchText.contains(value);
                    break;
                case ENDS_WITH:
                    result = matchText.endsWith(value);
                    break;
                case STARTS_WITH:
                    result = matchText.startsWith(value);
                    break;
                default:
                    break;
            }
            // Check if the result is false
            if (!result) {
                return false;
            }
        }
        return true;
    }
}
