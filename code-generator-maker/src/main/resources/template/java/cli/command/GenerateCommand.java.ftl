package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.file.MainGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

<#macro generateOption indent modelInfo>
    <#if modelInfo.description??>
        ${indent}/**
        ${indent} * ${modelInfo.description}
        ${indent} */
    </#if>
    ${indent}@CommandLine.Option(names = {<#if modelInfo.abbr??> "-${modelInfo.abbr}"</#if>,"--${modelInfo.fieldName}"}, arity = "0..1", <#if modelInfo.description??>description = "${modelInfo.description}",</#if>  echo = true, interactive = true)
    ${indent}public ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>



<#-- 生成命令调用 -->
<#macro generateCommand indent modelInfo>
    ${indent}System.out.println("输入${modelInfo.groupName}配置：");
    ${indent}CommandLine commandLine = new CommandLine(${modelInfo.type}Command.class);
    ${indent}commandLine.execute(${modelInfo.allArgsStr});
</#macro>

/**
* 生成配置命令
*
* @author ${author}
*/
@Data
@CommandLine.Command(name = "generate", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable
<Integer> {

    <#list modelConfig.models as modelInfo>

    <#-- 有分组 -->
        <#if modelInfo.groupKey??>

            static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();

            /**
            * ${modelInfo.description}
            */
            @CommandLine.Command(name = "${modelInfo.groupKey}")
            @Data
            public static class ${modelInfo.type}Command implements Runnable{
            <#list modelInfo.models as modelInfo>
                <@generateOption indent=" " modelInfo=modelInfo />
            </#list>

            @Override
            public void run() {
            <#list modelInfo.models as subModelInfo>
                ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
            </#list>
            }
            }
        <#-- 无分组 -->
        <#else>
            <@generateOption indent=" " modelInfo=modelInfo />
        </#if>

    </#list>


    @Override
    public Integer call() {
    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
            <#if modelInfo.condition??>
                if(${modelInfo.condition}){
                <@generateCommand indent=" " modelInfo=modelInfo/>
                }
            <#else>
                <@generateCommand indent=" " modelInfo=modelInfo/>
            </#if>
        </#if>
    </#list>
    DataModel dataModel = new DataModel();
    BeanUtil.copyProperties(this, dataModel);
    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
            dataModel.${modelInfo.groupKey} = ${modelInfo.groupKey};
        </#if>
    </#list>
    System.out.println("配置信息:" + dataModel);
    MainGenerator.doGenerate(dataModel);
    return 0;
    }
    }
