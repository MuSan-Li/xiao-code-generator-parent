package cn.xiao;

import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "test", mixinStandardHelpOptions = true)
@Data
public class TestArgGroupCommad implements Runnable {

    // 添加@ArgGroup类，用于标记分组
    @ArgGroup(exclusive = false, heading = "核心模板%n")
    MainTemplate mainTemplate;
    @Option(names = {"-n", "--needGit"}, arity = "0..1", description = "是否生成 .gitignore文件", interactive = true, echo = true)
    private boolean needGit = true;
    @Option(names = {"-l", "--loop"}, arity = "0..1", description = "是否生成循环", interactive = true, echo = true)
    private boolean loop = false;

    // main方法写调用逻辑
    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(TestArgGroupCommad.class);
//        commandLine.execute("-l", "-mainTemplate.a", "--mainTemplate.outputText");
        commandLine.execute("--help");
    }

    // run方法打印属性值
    @Override
    public void run() {
        System.out.println(needGit);
        System.out.println(loop);
        System.out.println(mainTemplate);
    }

    // 静态内部类，定义 mainTemplate.author 和 mainTemplate.outputText 属性
    @Data
    static class MainTemplate {
        @Option(names = {"-mainTemplate.a", "--mainTemplate.author"}, arity = "0..1", description = "作者注释", interactive = true, echo = true)
        private String author = "xiao";

        @Option(names = {"-mainTemplate.o", "--mainTemplate.outputText"}, arity = "0..1", description = "输出信息", interactive = true, echo = true)
        private String outputText = "sum = ";
    }
}