package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.file.MainGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * 生成配置命令
 *
 * @author ${author}
 */
@Data
@CommandLine.Command(name = "generate", mixinStandardHelpOptions = true)
public class GeneratorCommand implements Callable<Integer> {

<#list modelConfig.models as modelInfo>

<#if modelInfo.description??>
    /**
    * ${modelInfo.description}
    */
</#if>
    @CommandLine.Option(names = {<#if modelInfo.abbr??> "-${modelInfo.abbr}"</#if>,"-- ${modelInfo.fieldName}"}, arity = "0..1", <#if modelInfo.description??>description = "${modelInfo.description}",</#if>  echo = true, interactive = true)
    private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#list>


    @Override
    public Integer call() {
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        System.out.println("配置信息:" + dataModel);
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}
