package cn.xiao.codegenerator.basic.picocli;

import picocli.CommandLine;

import java.util.Arrays;

/**
 * 入门案例 非交互式
 * 交互式 Callable
 * 非交互式 Runnable
 *
 * @author xiao
 */
@CommandLine.Command(name = "PicocliExample", mixinStandardHelpOptions = true)
public class PicocliExample implements Runnable {

    @CommandLine.Option(names = {"-fs", "--fort-size"}, description = "文字大小")
    private int fontSize = 19;

    @CommandLine.Parameters(paramLabel = "<words>", description = "单词")
    private String[] words = {"Hello", "World"};

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PicocliExample()).execute(args);
        System.exit(exitCode);
    }

    public void run() {
        System.out.println("Font size: " + fontSize);
        System.out.println("words: " + Arrays.toString(words));
    }

}