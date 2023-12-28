package cn.xiao.maker;


import cn.xiao.maker.cli.CommandExecutor;

/**
 * main
 *
 * @author xiao
 */
public class BasicGeneratorMain {

    /**
     * main
     *
     * @param args
     */
    public static void main(String[] args) {
        // args = new String[]{"generate", "-l", "-a", "-o"};
        // args = new String[]{"config"};
        // args = new String[]{"list"};
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}