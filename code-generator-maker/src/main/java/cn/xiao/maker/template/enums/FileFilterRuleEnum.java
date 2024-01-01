package cn.xiao.maker.template.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 文件过滤规则枚举
 *
 * @author xiao
 */
@Getter
@AllArgsConstructor
public enum FileFilterRuleEnum {

    CONTAINS("包含", "contains"),

    STARTS_WITH("前缀匹配", "startsWith"),

    ENDS_WITH("后缀匹配", "endsWith"),

    REGEX("正则", "regex"),

    EQUALS("相等", "equals"),
    ;

    private final String text;
    private final String value;


    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static FileFilterRuleEnum getEnumByValue(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return Arrays.stream(FileFilterRuleEnum.values())
                .filter(anEnum -> anEnum.value.equals(value))
                .findFirst().orElse(null);
    }
}