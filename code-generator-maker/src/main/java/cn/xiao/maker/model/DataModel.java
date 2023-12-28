package cn.xiao.maker.model;

import lombok.Data;

/**
 * 动态模板配置
 *
 * @author xiao
 */
@Data
public class DataModel {

    /**
     * 作者
     */
    private String author;

    /**
     * 输出结果
     */
    private String outputText;

    /**
     * 是否循环
     */
    private boolean loop;

}
