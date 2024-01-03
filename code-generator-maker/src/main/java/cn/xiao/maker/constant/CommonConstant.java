package cn.xiao.maker.constant;

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

    public static final String MODEL_END_WITH = ".ftl";

    public static final String USER_DIR = "user.dir";

    public static final String WORK_SPACE = ".temp";

    public static final String FORMAT_FIELD_NAME = "${%s}";

    public static final String FORMAT_KV_FIELD_NAME = "${%s.%s}";

}
