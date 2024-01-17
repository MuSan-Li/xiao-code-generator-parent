package cn.xiao.cg.model.dto.generator;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 使用代码生成器请求
 *
 * @author xiao
 */
@Data
public class GeneratorUseRequest implements Serializable {

    /**
     * 生成器的 id
     */
    private Long id;

    /**
     * 数据模型
     */
    private Map<String, Object> dataModel;
}