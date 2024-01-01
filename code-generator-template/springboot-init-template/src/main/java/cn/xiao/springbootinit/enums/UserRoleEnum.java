package cn.xiao.springbootinit.enums;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 用户角色枚举
 *
 * @author xiao
 */
@Getter
@AllArgsConstructor
public enum UserRoleEnum {

    USER("用户", "user"),
    ADMIN("管理员", "admin"),
    BAN("被封号", "ban");

    private final String text;

    private final String value;

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (CharSequenceUtil.isBlank(value)) {
            return null;
        }
        return Arrays.stream(UserRoleEnum.values())
                .filter(anEnum -> anEnum.value.equals(value)).findFirst().orElse(null);
    }
}
