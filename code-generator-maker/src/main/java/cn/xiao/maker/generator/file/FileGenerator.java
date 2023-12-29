package cn.xiao.maker.generator.file;

import java.io.File;

/**
 * 静态动态组合生成
 *
 * @author xiao
 */
public class FileGenerator {
    private FileGenerator() {
    }


    public static void doGenerator(Object model) {
        try {
            String projectProperty = System.getProperty("user.dir");
            String srcPath = new File(projectProperty).getParent() + File.separator + "code-generator-template" + File.separator + "acm-template";
            String destPath = projectProperty + File.separator + "templateOutput";
            StaticFileGenerator.doGenerate(srcPath, destPath);
            String inputPath = projectProperty + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
            String outputPath = projectProperty + File.separator + "templateOutput/acm-template/src/main/java/cn/xiao/MainTemplate.java";

            DynamicFileGenerator.doGenerate(inputPath, outputPath, model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
