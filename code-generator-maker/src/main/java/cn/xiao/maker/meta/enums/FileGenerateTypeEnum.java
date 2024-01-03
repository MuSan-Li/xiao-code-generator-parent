package cn.xiao.maker.meta.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

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

    public static FileGenerateTypeEnum getEnumByValue(String value) {
        return Arrays.stream(FileGenerateTypeEnum.values())
                .filter(item -> Objects.equals(item.getValue(), value))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

}