package cn.xiao.springbootinit.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author xiao
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;
}