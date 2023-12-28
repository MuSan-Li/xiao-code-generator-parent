package cn.xiao.codegenerator.basic.picocli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * 嵌套命令
 *
 * @author 小小
 */
@Command(name = "NestCommandExample", mixinStandardHelpOptions = true)
public class NestCommandExample implements Runnable {

    @Override
    public void run() {
        System.out.println("执行主命令");
    }

    @Command(name = "-add", description = "增加", mixinStandardHelpOptions = true)
    static class AddCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("执行增加命令");
        }
    }

    @Command(name = "-query", description = "查询", mixinStandardHelpOptions = true)
    static class QueryCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("执行查询命令");
        }
    }

    @Command(name = "-delete", description = "删除", mixinStandardHelpOptions = true)
    static class DeleteCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("执行删除命令");
        }
    }


    public static void main(String[] args) {
        // 执行主命令
        List<String> argList = new ArrayList<>();
        // 查看主命令的帮助手册
        // argList.add("--help");
        // 执行增加命令
        // argList.add("-add");
        // 执行查询命令的帮助手册
        // argList.add("-query");
        // argList.add("--help");
        // 执行删除命令
        // argList.add("-delete");
        // 执行不存在的命令，会报错
        argList.add("-update");


        int exitCode = new CommandLine(new NestCommandExample())
                .addSubcommand(new AddCommand())
                .addSubcommand(new DeleteCommand())
                .addSubcommand(new QueryCommand())
                .execute(argList.toArray(new String[0]));
        System.exit(exitCode);
    }
}