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
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        Meta meta = JSONUtil.toBean(metaJson, Meta.class);
        // TODO 校验meta对象
        return meta;
    }


    /**
     * test
     *
     * @param args
     */
    public static void main(String[] args) {
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        Meta bean = JSONUtil.toBean(metaJson, Meta.class);
        System.out.println(bean);
    }
}
