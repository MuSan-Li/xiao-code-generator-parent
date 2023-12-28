package cn.xiao.cg.basic.generator;

import cn.xiao.cg.basic.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 静态动态组合生成
 *
 * @author xiao
 */
public class MainGenerator {
    private MainGenerator() {
    }

    /**
     * main
     *
     * @param args
     * @throws IOException
     * @throws TemplateException
     */
    public static void main(String[] args) throws IOException, TemplateException {
        MainTemplateConfig model = new MainTemplateConfig();
        model.setAuthor("小小");
        model.setOutputText("输出结果：");
        model.setLoop(false);
        doGenerator(model);
    }


    public static void doGenerator(Object model) {
        try {
            String projectProperty = System.getProperty("user.dir");
            String srcPath = new File(projectProperty).getParent() + File.separator + "code-generator-template" + File.separator + "acm-template";
            String destPath = projectProperty + File.separator + "templateOutput";
            StaticGenerator.copyFileByRecursion(srcPath, destPath);

            String inputPath = projectProperty + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
            String outputPath = projectProperty + File.separator + "templateOutput/acm-template/src/main/java/cn/xiao/MainTemplate.java";

            DynamicGenerator.doGenerate(inputPath, outputPath, model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
