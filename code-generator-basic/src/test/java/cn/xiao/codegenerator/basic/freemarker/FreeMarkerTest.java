package cn.xiao.codegenerator.basic.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * freemarker Tests
 *
 * @author xiao
 */
public class FreeMarkerTest {

    /**
     * freemarker快速入门
     *
     * @throws IOException
     * @throws TemplateException
     */
    @Test
    public void freemarkerQuickStart() throws IOException, TemplateException {
        //new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        // 1.指定模板文件所在的路径
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));

        // 2.设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        configuration.setNumberFormat("0.######");

        // 3.创建模板对象，加载指定模板
        Template template = configuration.getTemplate("myweb.html.ftl");

        // 4.创建数据模型，指定当前的年份和导航栏菜单项
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("currentYear", 2023);
        List<Map<String, Object>> menuItems = new ArrayList<>();

        Map<String, Object> menuItem = new HashMap<>();
        menuItem.put("url", "https://exotisch.cn");
        menuItem.put("label", "小小博客");

        menuItems.add(menuItem);

        dataModel.put("menuItems", menuItems);

        // 5.指定生成的文件
        Writer out = new FileWriter("templateOutput" + File.separator + "myweb.html");

        // 6.调用process方法，处理并生成文件
        template.process(dataModel, out);

        // 7.关闭
        out.close();
    }
}