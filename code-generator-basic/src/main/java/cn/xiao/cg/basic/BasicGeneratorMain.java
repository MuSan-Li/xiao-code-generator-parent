package cn.xiao.cg.basic;

import cn.xiao.cg.basic.cli.CommandExecutor;

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