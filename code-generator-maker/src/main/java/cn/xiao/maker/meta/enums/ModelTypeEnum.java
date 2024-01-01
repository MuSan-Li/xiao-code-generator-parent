package cn.xiao.maker.meta.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模型类型枚举
 *
 * @author xiao
 */
@Getter
@AllArgsConstructor
public enum ModelTypeEnum {

    STRING("字符串", "String"),

    BOOLEAN("布尔值", "boolean");

    private final String text;
    private final String value;
}
