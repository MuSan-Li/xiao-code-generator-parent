package cn.xiao.maker.meta.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件生成类型枚举
 *
 * @author xiao
 */
@Getter
@AllArgsConstructor
public enum FileGenerateTypeEnum {

    DYNAMIC("动态", "dynamic"),
    STATIC("静态", "static");

    private final String text;
    private final String value;
}