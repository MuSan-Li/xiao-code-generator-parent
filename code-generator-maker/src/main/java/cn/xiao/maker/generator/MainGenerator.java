package cn.xiao.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.xiao.maker.generator.file.DynamicFileGenerator;
import cn.xiao.maker.meta.Meta;
import cn.xiao.maker.meta.MetaManager;

import java.io.File;

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
        String outputBasePackagePath = CharSequenceUtil.join(StrPool.SLASH, CharSequenceUtil.split(outputBasePackage, StrPool.DOT));
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        // 生成文件
        // model.DataModel
        inputFilePath = inputResourcePath + File.separator + "template/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);


        // cli.CommandExecutor
        inputFilePath = inputResourcePath + File.separator + "template/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);


        // cli.command.ConfigCommand
        inputFilePath = inputResourcePath + File.separator + "template/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);


        // cli.command.GeneratorCommand
        inputFilePath = inputResourcePath + File.separator + "template/java/cli/command/GeneratorCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/GeneratorCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ListGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generate.file.MainGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/generate/file/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generate/file/MainGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generate.file.DynamicGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/generate/file/DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generate/file/DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generate.file.StaticGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/generate/file/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generate/file/StaticGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // GeneratorMain
        inputFilePath = inputResourcePath + File.separator + "template/java/GeneratorMain.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/GeneratorMain.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // pom.xml
        inputFilePath = inputResourcePath + File.separator + "template/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        // 构建jar包
        JarGenerator.doGenerate(outputPath);

        // script文件
        String shellOutputPath = outputPath + File.separator + "generator";
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "./target/" + jarName;
        ScriptGenerator.doGenerate(shellOutputPath, jarPath);

        // 复制原始文件模板文件到.source路径下
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + meta.getFileConfig().getInputRootPath();
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, true);
    }
}
