package cn.xiao.codegenerator.basic.generator;

import cn.hutool.core.util.CharsetUtil;
import cn.xiao.codegenerator.basic.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 动态生成
 *
 * @author xiao
 */
public class DynamicGenerator {

    private DynamicGenerator() {

    }

    public static void main(String[] args) throws TemplateException, IOException {
        String projectProperty = System.getProperty("user.dir") + File.separator + "code-generator-basic";
        String inputPath = projectProperty + File.separator + "/src/main/resources/templates/MainTemplate.java.ftl";
        String outputPath = projectProperty + File.separator + "templateOutput" + File.separator + "MainTemplate.java";
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("小小");
        mainTemplateConfig.setOutputText("输出结果：");
        mainTemplateConfig.setLoop(false);
        doGenerate(inputPath, outputPath, mainTemplateConfig);
    }


    /**
     * 生成文件
     *
     * @param inputPath
     * @param outputPath
     * @param model
     */
    public static void doGenerate(String inputPath, String outputPath, Object model)
            throws IOException, TemplateException {
        //new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        // 1.指定模板文件所在的路径
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        // 2.设置模板文件使用的字符集
        configuration.setDefaultEncoding(CharsetUtil.UTF_8);

        configuration.setNumberFormat("0.######");

        // 3.创建模板对象，加载指定模板
        String fileName = new File(inputPath).getName();
        Template template = configuration.getTemplate(fileName);

        // 5.指定生成的文件
        Writer out = new FileWriter(outputPath);

        // 6.调用process方法，处理并生成文件
        template.process(model, out);

        // 7.关闭
        out.close();
        System.out.println("==== generator dynamic file success ====");
    }

}
