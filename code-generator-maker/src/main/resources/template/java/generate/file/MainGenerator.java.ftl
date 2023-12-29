package ${basePackage}.generator.file;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 静态动态组合生成
 *
 * @author ${author}
 */
public class MainGenerator {

    private MainGenerator() {
    }

    public static void doGenerate(Object model) {
        try {
            String inputRootPath = "${fileConfig.inputRootPath}";
            String outputFileName = new File(inputRootPath).getName();
            String outputRootPath = "${fileConfig.outputRootPath}" + File.separator + outputFileName;

            String inputPath;
            String outputPath;

        <#list fileConfig.files as fileInfo>
            inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
            <#if fileInfo.generateType=="static">
            StaticGenerator.doGenerate(inputPath,outputPath);
            <#else>
            DynamicGenerator.doGenerate(inputPath, outputPath, model);
            </#if>
        </#list>
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
