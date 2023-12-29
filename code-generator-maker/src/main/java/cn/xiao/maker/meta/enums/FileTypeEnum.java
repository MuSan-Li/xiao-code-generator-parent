package cn.xiao.maker.meta.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件类型枚举
 *
 * @author xiao
 */
@Getter
@AllArgsConstructor
public enum FileTypeEnum {

    DIR("目录", "dir"),
    FILE("文件", "file"),
    GROUP("分组", "group"),
    ;

    private final String text;
    private final String value;
}