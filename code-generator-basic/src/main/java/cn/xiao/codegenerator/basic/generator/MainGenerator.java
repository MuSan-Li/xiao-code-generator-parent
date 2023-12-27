package cn.xiao.codegenerator.basic.generator;

import cn.xiao.codegenerator.basic.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    private MainGenerator() {
    }

    public static void main(String[] args) throws IOException, TemplateException {
        String projectProperty = System.getProperty("user.dir");
        String srcPath = projectProperty + File.separator + "code-generator-template" + File.separator + "acm-template";
        String destPath = projectProperty + File.separator + "code-generator-basic" + File.separator + "templateOutput";
        StaticGenerator.copyFileByRecursion(srcPath, destPath);

        String dynamicProjectProperty = System.getProperty("user.dir") + File.separator + "code-generator-basic";
        String inputPath = dynamicProjectProperty + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String outputPath = dynamicProjectProperty + File.separator + "templateOutput/acm-template/src/main/java/cn/xiao/MainTemplate.java";
        MainTemplateConfig model = new MainTemplateConfig();
        model.setAuthor("小小");
        model.setOutputText("输出结果：");
        model.setLoop(false);
        DynamicGenerator.doGenerate(inputPath, outputPath, model);
    }
}
