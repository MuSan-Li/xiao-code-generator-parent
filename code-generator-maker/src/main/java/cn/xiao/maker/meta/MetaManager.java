package cn.xiao.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

import java.util.Objects;

/**
 * 使用单例模式初始化Meta对象 不用每次调用都初始化 节约性能
 *
 * @author xiao
 */
public class MetaManager {

    /**
     * 内存可见性 多个线程的本地副本可见
     * 多个线程同时进来 只会进一次 进一次后 直接返回
     * 一个线程修改 其他线程立马可见
     */
    private static volatile Meta meta;

    private MetaManager() {
        // 私有构造函数 防止外部实例化
    }

    /**
     * 双检锁单例模式
     *
     * @return
     */
    public static Meta getMetaObject() {
        // 加锁是因为节约性能损耗 不必多次进来都要直接加锁初始化
        if (Objects.isNull(meta)) {
            synchronized (MetaManager.class) {
                if (Objects.isNull(meta)) {
                    meta = initMetaObject();
                }
            }
        }
        return meta;
    }


    private static Meta initMetaObject() {
        // String metaJson = ResourceUtil.readUtf8Str(CommonConstant.META_NAME);
        // String metaJson = ResourceUtil.readUtf8Str("spring-boot-init-meta.json");
        String metaJson = ResourceUtil.readUtf8Str("acm-init-meta.json");
        Meta meta = JSONUtil.toBean(metaJson, Meta.class);
        // 校验meta对象
        MetaValidator.doValidaAndFill(meta);
        return meta;
    }
}
