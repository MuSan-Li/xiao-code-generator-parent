package cn.xiao.maker.template.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 模板生成器输出配置
 *
 * @author xiao
 */
@Data
public class TemplateMakerOutputConfig implements Serializable {

    /**
     * 从未分组文件中移除组内的同名文件
     */
    private boolean removeGroupFilesFromRoot = true;
}