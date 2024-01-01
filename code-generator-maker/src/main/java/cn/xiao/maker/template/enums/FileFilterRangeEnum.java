package cn.xiao.maker.template.enums;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 文件过滤范围枚举
 *
 * @author xiao
 */
@Getter
@AllArgsConstructor
public enum FileFilterRangeEnum {

    FILE_NAME("文件名称", "fileName"),

    FILE_CONTENT("文件内容", "fileContent"),
    ;

    private final String text;
    private final String value;


    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static FileFilterRangeEnum getEnumByValue(String value) {
        if (CharSequenceUtil.isBlank(value)) {
            return null;
        }
        return Arrays.stream(FileFilterRangeEnum.values())
                .filter(anEnum -> anEnum.value.equals(value))
                .findFirst().orElse(null);
    }
}