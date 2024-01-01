package cn.xiao.springbootinit.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 *
 * @author xiao
 */
@Data
public class UploadFileRequest implements Serializable {


    /**
     * 业务
     */
    private String biz;
}