package cn.xiao.maker.cli.command;

import cn.hutool.core.bean.BeanUtil;
import cn.xiao.maker.generator.file.FileGenerator;
import cn.xiao.maker.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * 生成配置命令
 *
 * @author xiao
 */
@Data
@CommandLine.Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
public class GeneratorCommand implements Callable<Integer> {

    /**
     * 作者
     */
    @CommandLine.Option(names = {"-a", "--author"}, arity = "0..1", description = "作者", prompt = "请输入作者:", echo = true, interactive = true)
    private String author;

    /**
     * 输出结果
     */
    @CommandLine.Option(names = {"-o", "--outputText"}, arity = "0..1", description = "输出结果", echo = true, interactive = true)
    private String outputText;

    /**
     * 是否循环
     */
    @CommandLine.Option(names = {"-l", "--loop"}, arity = "0..1", description = "是否循环", echo = true, interactive = true)
    private boolean loop;


    @Override
    public Integer call() {
        DataModel mainTemplateConfig = new DataModel();
        BeanUtil.copyProperties(this, mainTemplateConfig);
        System.out.println("配置信息:" + mainTemplateConfig);
        FileGenerator.doGenerator(mainTemplateConfig);
        return 0;
    }
}
