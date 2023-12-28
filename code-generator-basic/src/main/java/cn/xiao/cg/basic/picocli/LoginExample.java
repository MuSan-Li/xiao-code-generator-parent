package cn.xiao.cg.basic.picocli;

import cn.hutool.core.util.ObjectUtil;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * 入门案例 交互式
 *
 * @author 小小
 */
//1.首先命令类需要实现 Callable 接口
public class LoginExample implements Callable<Integer> {
    @Option(names = {"-u", "--username"}, description = "用户名")
    String user;

    //2.将 @Option 注解的 interactive 参数设置为 true，表示该选项支持交互式输入
    @Option(names = {"-p", "--password"}, description = "密码", interactive = true, prompt = "请输入密码:", arity = "0..1")
    String password;

    //2.将 @Option 注解的 interactive 参数设置为 true，表示该选项支持交互式输入 prompt 参数引导用户输入的提示语
    @Option(names = {"-cp", "--checkPassword"}, description = "校验密码", interactive = true, prompt = "请输入校验密码:", arity = "0..1")
    String checkPassword;

    //4.在 BasicGeneratorMain 方法中执行命令并传入参数
    public static void main(String[] args) {
        args = new String[]{"-u", "user123", "-p"};
        args = processInteractiveOptions(LoginExample.class, args);
        new CommandLine(new LoginExample()).execute(args);
    }

    /**
     * 若用户为输入校验密码则强制校验
     *
     * @param clazz
     * @param args
     * @return
     */
    public static String[] processInteractiveOptions(Class<?> clazz, String[] args) {
        if (ObjectUtil.hasEmpty(clazz, args)) {
            throw new RuntimeException("参数为空");
        }
        Set<String> argSet = new LinkedHashSet<>(Arrays.asList(args));
        for (Field field : clazz.getDeclaredFields()) {
            Option annotation = field.getAnnotation(Option.class);
            if (ObjectUtil.isEmpty(annotation)) {
                continue;
            }
            boolean interactive = annotation.interactive();
            if (!interactive) {
                continue;
            }
            String[] names = annotation.names();
            if (ObjectUtil.isEmpty(names)) {
                continue;
            }
            argSet.add(names[0]);
        }
        return argSet.toArray(new String[0]);
    }

    @Override
    //3.所有参数都输入完成后，会执行 call 方法，可以在方法中编写业务逻辑
    public Integer call() throws Exception {
        // 打印出密码
        System.out.println("密码是：" + password);
        System.out.println("校验密码：" + checkPassword);
        return 0;
    }

}