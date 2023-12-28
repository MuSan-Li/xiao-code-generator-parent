package cn.xiao.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import cn.xiao.maker.generator.file.DynamicFileGenerator;
import cn.xiao.maker.meta.Meta;
import cn.xiao.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.sql.Struct;

/**
 * 生成器入口
 *
 * @author xiao
 */
public class MainGenerator {

    /**
     * 主函数
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // 获取元信息数据
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);

        // 输出根路径
        String projectProperty = System.getProperty("user.dir");
        String outputPath = projectProperty + File.separator + meta.getFileConfig().getOutputRootPath() + File.separator + meta.getName();
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 读取resource目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // Java包基础路径
        String outputBasePackage = meta.getBasePackage();
        String outputBasePackagePath = StrUtil.join(StrUtil.SLASH, StrUtil.split(outputBasePackage, StrUtil.DOT));
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        // 生成文件
        // model.DataModel
        inputFilePath = inputResourcePath + File.separator + "template/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
    }
}
