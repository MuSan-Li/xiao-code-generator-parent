package ${basePackage}.generator.file;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import ${basePackage}.model.DataModel;


<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType=="static">
${indent}StaticGenerator.doGenerate(inputPath,outputPath);
<#else>
${indent}DynamicGenerator.doGenerate(inputPath, outputPath, model);
</#if>
</#macro>

/**
 * 静态动态组合生成
 *
 * @author ${author}
 */
public class MainGenerator {

    private MainGenerator() {
    }

    public static void doGenerate(DataModel model) {
        try {
            String inputRootPath = "${fileConfig.inputRootPath}";
            String outputFileName = new File(inputRootPath).getName();
            String outputRootPath = "${fileConfig.outputRootPath}" + File.separator + outputFileName;

            String inputPath;
            String outputPath;

<#list modelConfig.models as modelInfo>
            ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
</#list>

<#list fileConfig.files as fileInfo>
    <#if fileInfo.groupKey??>
        <#if fileInfo.condition??>
            if(${fileInfo.condition}){
            <#list fileInfo.files as fileInfo>
                <@generateFile indent="                     " fileInfo=fileInfo />
            </#list>
            }

            <#else >

                <@generateFile indent="        " fileInfo=fileInfo/>

            </#if>

        <#else>
            <#if fileInfo.condition??>
            if(${fileInfo.condition}){
                <@generateFile indent="            " fileInfo=fileInfo/>
            }
            <#else>
                <@generateFile indent="             " fileInfo=fileInfo/>
        </#if>
    </#if>
</#list>

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
