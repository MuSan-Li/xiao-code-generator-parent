package cn.xiao.cg.basic.cli.command;


import cn.hutool.core.util.ReflectUtil;
import cn.xiao.cg.basic.model.MainTemplateConfig;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * 查看参数信息
 *
 * @author xiao
 */
@CommandLine.Command(name = "config", description = "查看参数信息", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

    @Override
    public void run() {
        //实现 config 命令的逻辑
        System.out.println("查看参数信息");
        Field[] fields = ReflectUtil.getFields(MainTemplateConfig.class);
        for (Field field : fields) {
            System.out.println("字段名称：" + field.getName() + ", 字段类型：" + field.getType());
        }
    }
}
