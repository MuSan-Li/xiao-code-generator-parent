package cn.xiao.maker.constant;

import cn.hutool.core.text.StrPool;
import com.sun.org.apache.xml.internal.utils.LocaleUtility;

/**
 * 常量类
 *
 * @author xiao
 */
public abstract class CommonConstant {


    private CommonConstant() {
    }

    /**
     * 元信息名称
     */
    public static final String META_NAME = "meta.json";

    /**
     * 模板文件后缀
     */
    public static final String MODEL_END_WITH = ".ftl";

    /**
     * 根目录
     */
    public static final String USER_DIR = "user.dir";

    /**
     * 模板空间
     */
    public static final String WORK_SPACE = ".temp";

    /**
     * 替换变量
     */
    public static final String FORMAT_FIELD_NAME = "${%s}";

    /**
     * 替换变量 kv
     */
    public static final String FORMAT_KV_FIELD_NAME = "${%s.%s}";

    /**
     * \\\\字符
     */
    public static final String BACK_SLASH = StrPool.BACKSLASH + StrPool.BACKSLASH;
    /**
     * "" 字符
     */
    public static final String BLANK = LocaleUtility.EMPTY_STRING;

}
