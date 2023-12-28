package cn.xiao.maker.generator.file;

import cn.xiao.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 静态动态组合生成
 *
 * @author xiao
 */
public class FileGenerator {
    private FileGenerator() {
    }

    /**
     * main
     *
     * @param args
     * @throws IOException
     * @throws TemplateException
     */
    public static void main(String[] args) throws IOException, TemplateException {
        DataModel model = new DataModel();
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
            StaticFileGenerator.doGenerator(srcPath, destPath);
            String inputPath = projectProperty + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
            String outputPath = projectProperty + File.separator + "templateOutput/acm-template/src/main/java/cn/xiao/MainTemplate.java";

            DynamicFileGenerator.doGenerate(inputPath, outputPath, model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
