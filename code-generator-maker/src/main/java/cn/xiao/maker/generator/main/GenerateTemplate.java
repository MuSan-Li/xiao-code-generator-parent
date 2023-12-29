package cn.xiao.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.xiao.maker.generator.JarGenerator;
import cn.xiao.maker.generator.ScriptGenerator;
import cn.xiao.maker.generator.file.DynamicFileGenerator;
import cn.xiao.maker.generator.file.StaticFileGenerator;
import cn.xiao.maker.meta.Meta;
import cn.xiao.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public abstract class GenerateTemplate {

    /**
     * 主流程
     *
     * @throws Exception
     */
    public void doGenerate() throws Exception {
        // 获取元信息数据
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);

        // 输出根路径
        String projectProperty = System.getProperty("user.dir");
        String outputPath = projectProperty + File.separator +
                meta.getFileConfig().getOutputRootPath() + File.separator + meta.getName();

        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // Java包基础路径
        String outputBasePackagePath = StrUtil.join(StrPool.SLASH, StrUtil.split(meta.getBasePackage(), StrPool.DOT));
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputBasePackagePath;

        // 生成代码
        generateCode(outputBaseJavaPackagePath, meta, outputPath);

        // 构建jar包
        JarGenerator.doGenerate(outputPath);

        // 复制原始文件模板文件到.source路径下
        String destSourcePath = copySource(meta, outputPath);

        // 生成script文件
        String jarName = buildScript(meta, outputPath);

        // 制作精简代码包
        buildDist(outputPath, jarName, destSourcePath);
    }


    /**
     * 生成文件
     *
     * @param outputBaseJavaPackagePath
     * @param meta
     * @param outputPath
     * @throws IOException
     * @throws TemplateException
     */
    protected void generateCode(String outputBaseJavaPackagePath,
                                Meta meta, String outputPath) throws IOException, TemplateException {

        // 读取resource目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        String inputFilePath;
        String outputFilePath;

        // 生成文件
        // model.DataModel
        inputFilePath = inputResourcePath + File.separator + "template/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generate.file.MainGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/generator/file/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/file/MainGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generate.file.DynamicGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/generator/file/DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/file/DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generate.file.StaticGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/generator/file/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/file/StaticGenerator.java";
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
        inputFilePath = inputResourcePath + File.separator + "template/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ListGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // GeneratorMain
        inputFilePath = inputResourcePath + File.separator + "template/java/GeneratorMain.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/GeneratorMain.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // pom.xml
        inputFilePath = inputResourcePath + File.separator + "template/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // gitignore
        inputFilePath = inputResourcePath + File.separator + "template/.gitignore";
        outputFilePath = outputPath + File.separator + ".gitignore";
        StaticFileGenerator.doGenerate(inputFilePath, outputFilePath);

        // 生成README.md文件
        inputFilePath = inputResourcePath + File.separator + "template/README.md.ftl";
        outputFilePath = outputPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
    }


    /**
     * 制作精简代码包
     *
     * @param outputPath
     * @param jarName
     * @param sourceCopyDestPath
     */
    protected void buildDist(String outputPath, String jarName, String sourceCopyDestPath) {
        String distOutputPath = outputPath + "-dist";

        // 复制jar包
        String targetPath = distOutputPath + File.separator + "target";
        FileUtil.mkdir(targetPath);
        String sourceJarPath = outputPath + File.separator + "target" + File.separator + jarName;
        FileUtil.copy(sourceJarPath, targetPath, true);

        // 复制maven脚本路径
        String mavenScriptOutputPath = outputPath + File.separator + "generator";
        FileUtil.copy(mavenScriptOutputPath + ".sh", distOutputPath, true);
        FileUtil.copy(mavenScriptOutputPath + ".bat", distOutputPath, true);

        // 复制git脚本路径
        String gitScriptOutputPath = outputPath + File.separator + "git-init";
        FileUtil.copy(gitScriptOutputPath + ".sh", distOutputPath, true);
        FileUtil.copy(gitScriptOutputPath + ".bat", distOutputPath, true);

        // 复制模板文件
        FileUtil.copy(sourceCopyDestPath, distOutputPath, true);
    }


    /**
     * 复制原始文件模板文件到.source路径下
     *
     * @param meta
     * @param outputPath
     * @return
     */
    private static String copySource(Meta meta, String outputPath) {
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String destSourcePath = outputPath + File.separator + new File(meta.getFileConfig().getInputRootPath()).getParent();
        FileUtil.copy(sourceRootPath, destSourcePath, true);
        return destSourcePath;
    }

    /**
     * 生成script文件
     *
     * @param meta
     * @param outputPath
     * @return
     * @throws IOException
     */
    private static String buildScript(Meta meta, String outputPath) throws IOException {
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "./target/" + jarName;
        ScriptGenerator.doGenerate(outputPath, jarPath);
        return jarName;
    }

}