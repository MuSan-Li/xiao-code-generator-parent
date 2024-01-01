package cn.xiao.maker.template.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件过滤配置
 *
 * @author xiao
 */
@Data
@Builder
public class FileFilterConfig implements Serializable {

    /**
     * 过滤范围
     */
    private String range;
    /**
     * 过滤规则
     */
    private String rule;
    /**
     * 过滤值
     */
    private String value;
}