package cn.xiao.maker.meta;

/**
 * meta配置文件异常处理
 *
 * @author xiao
 */
public class MetaException extends RuntimeException {

    public MetaException(String message) {
        super(message);
    }

    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }
}
