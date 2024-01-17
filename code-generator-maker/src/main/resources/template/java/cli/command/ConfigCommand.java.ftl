package ${basePackage}.cli.command;


import cn.hutool.core.util.ReflectUtil;
import ${basePackage}.model.DataModel;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
* 查看参数信息
*
* @author ${author}
*/
@CommandLine.Command(name = "config", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

@Override
public void run() {
//实现 config 命令的逻辑
System.out.println("查看参数信息");
Field[] fields = ReflectUtil.getFields(DataModel.class);
for (Field field : fields) {
System.out.println("字段名称：" + field.getName() + ", 字段类型：" + field.getType());
}
}
}
