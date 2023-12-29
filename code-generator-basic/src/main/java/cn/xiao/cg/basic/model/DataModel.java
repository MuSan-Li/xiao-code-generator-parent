package cn.xiao.cg.basic.model;

import lombok.Data;


/**
 * 动态模板配置
 *
 * @author xiao
 */
@Data
public class DataModel {


    /**
     * 是否生成.gitignore文件
     */
    public boolean needGit = true;


    /**
     * 是否生成循环
     */
    public boolean loop;


    /**
     * 核心模板
     */
    public MainTemplate mainTemplate = new MainTemplate();

    /**
     * 用于生成核心模板文件
     */
    @Data
    public static class MainTemplate {
        /**
         * 作者注释
         */
        public String author = "xiao";
        /**
         * 输出信息
         */
        public String outputText = "sum = ";
    }
}
