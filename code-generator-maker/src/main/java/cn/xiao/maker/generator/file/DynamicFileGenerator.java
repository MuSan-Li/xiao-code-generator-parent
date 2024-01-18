package cn.xiao.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 动态生成
 *
 * @author xiao
 */
public class DynamicFileGenerator {

    private DynamicFileGenerator() {

    }

    /**
     * 生成文件
     *
     * @param relativeInputPath
     * @param outputPath
     * @param model
     */
    public static void doGenerate(String relativeInputPath, String outputPath, Object model)
            throws IOException, TemplateException {
        //new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        // 获取模板文件所属包和模板名称
        int lastIndexOf = relativeInputPath.lastIndexOf("/");
        String basePackagePath = relativeInputPath.substring(0, lastIndexOf);
        String templateName = relativeInputPath.substring(lastIndexOf + 1);

        // 通过类加载器读取模板
        ClassTemplateLoader loader = new ClassTemplateLoader(DynamicFileGenerator.class, basePackagePath);
        configuration.setTemplateLoader(loader);

        // 2.设置模板文件使用的字符集
        configuration.setDefaultEncoding(CharsetUtil.UTF_8);

        configuration.setNumberFormat("0.######");

        // 3.创建模板对象，加载指定模板
        Template template = configuration.getTemplate(templateName, CharsetUtil.UTF_8);

        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }
        // 5.指定生成的文件 中文乱码
        // Writer out = new FileWriter(outputPath);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)),
                StandardCharsets.UTF_8));
        // 6.调用process方法，处理并生成文件
        template.process(model, out);

        // 7.关闭
        out.close();
        System.out.println("==== generator dynamic file success ====");
    }

    /**
     * 生成文件
     *
     * @param inputPath
     * @param outputPath
     * @param model
     */
    public static void doGeneratePath(String inputPath, String outputPath, Object model)
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
        Template template = configuration.getTemplate(fileName, CharsetUtil.UTF_8);

        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }
        // 5.指定生成的文件 中文乱码
        // Writer out = new FileWriter(outputPath);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)),
                StandardCharsets.UTF_8));
        // 6.调用process方法，处理并生成文件
        template.process(model, out);

        // 7.关闭
        out.close();
        System.out.println("==== generator dynamic file success ====");
    }

}
