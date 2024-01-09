package cn.xiao.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.xiao.maker.constant.CommonConstant;
import cn.xiao.maker.generator.JarGenerator;
import cn.xiao.maker.generator.ScriptGenerator;
import cn.xiao.maker.generator.file.DynamicFileGenerator;
import cn.xiao.maker.meta.Meta;
import cn.xiao.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 生成模板主类
 *
 * @author xiao
 */
public abstract class GenerateTemplate {

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
        FileUtil.copy(sourceRootPath, destSourcePath, false);
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
        String projectProperty = System.getProperty(CommonConstant.USER_DIR);
        String rootPath = projectProperty + File.separator +
                meta.getFileConfig().getOutputRootPath() + File.separator + meta.getName();

        if (!FileUtil.exist(rootPath)) {
            FileUtil.mkdir(rootPath);
        }

        // 复制原始文件模板文件到.source路径下
        String destSourcePath = copySource(meta, rootPath);

        // 生成代码
        generateCode(meta, rootPath);

        // 构建jar包
        JarGenerator.doGenerate(rootPath);

        // 生成script文件
        String jarName = buildScript(meta, rootPath);

        // 制作精简代码包
        String distOutputPath = buildDist(rootPath, jarName, destSourcePath);

        // 制作ZIP压缩包
        // buildZipPackage(distOutputPath);
    }

    /**
     * 生成文件
     *
     * @param meta
     * @param outputPath
     * @throws IOException
     * @throws TemplateException
     */
    protected void generateCode(Meta meta, String outputPath) throws Exception {

        // 读取resource目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // Java 包基础路径
        String outputBasePackage = meta.getBasePackage();
        String outputBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage, "."));
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputBasePackagePath;

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
    }

    // /**
    //  * 生成文件
    //  *
    //  * @param rootPath
    //  * @param meta
    //  * @throws IOException
    //  * @throws TemplateException
    //  */
    // protected void generateCode(String rootPath,
    //                             Meta meta) throws Exception {
    //     List<FilesDTO> filesDTOList = new ArrayList<>();
    //
    //     List<FilesDTO> filesDTOS = meta.getFileConfig().getFiles();
    //     if (CollUtil.isNotEmpty(filesDTOS)) {
    //         Set<FilesDTO> filesDTOSet = filesDTOS.stream()
    //                 .filter(item -> StrUtil.isBlank(item.getGroupKey()))
    //                 .collect(Collectors.toSet());
    //         filesDTOS.stream()
    //                 .filter(item -> StrUtil.isNotBlank(item.getGroupKey()))
    //                 .flatMap(item -> item.getFiles().stream())
    //                 .forEach(filesDTOSet::add);
    //         filesDTOList = new ArrayList<>(filesDTOSet);
    //     }
    //
    //     for (FilesDTO filesDTO : filesDTOList) {
    //         // 生成文件
    //         String inputFilePath = rootPath + File.separator + filesDTO.getInputPath();
    //         String outputFilePath = rootPath + File.separator + filesDTO.getOutputPath();
    //         String generateType = filesDTO.getGenerateType();
    //         FileGenerateTypeEnum generateTypeEnum = FileGenerateTypeEnum.getEnumByValue(generateType);
    //         if (Objects.equals(FileGenerateTypeEnum.STATIC, generateTypeEnum)) {
    //             StaticFileGenerator.doGenerate(inputFilePath, outputFilePath);
    //             continue;
    //         }
    //         DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
    //     }
    // }

    /**
     * 制作精简代码包
     *
     * @param outputPath
     * @param jarName
     * @param sourceCopyDestPath
     */
    protected String buildDist(String outputPath, String jarName, String sourceCopyDestPath) {
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
        return distOutputPath;
    }


    /**
     * 制作zip 压缩包
     *
     * @param outputPath
     */
    protected String buildZipPackage(String outputPath) {
        String zipPath = outputPath + ".zip";
        ZipUtil.zip(outputPath, zipPath);
        return zipPath;
    }

}