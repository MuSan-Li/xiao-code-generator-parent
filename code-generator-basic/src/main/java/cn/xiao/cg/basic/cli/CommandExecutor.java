package cn.xiao.cg.basic.cli;

import cn.xiao.cg.basic.cli.command.ConfigCommand;
import cn.xiao.cg.basic.cli.command.GeneratorCommand;
import cn.xiao.cg.basic.cli.command.JsonGenerateCommand;
import cn.xiao.cg.basic.cli.command.ListCommand;
import picocli.CommandLine;

/**
 * 命令执行器
 *
 * @author xiao
 */
@CommandLine.Command(name = "CommandExecutor", description = "命令执行器", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GeneratorCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand())
                .addSubcommand(new JsonGenerateCommand());
    }

    @Override
    public void run() {
        // 不输入子命令时，给出友好提示
        System.out.println("请输入具体命令，或者输入 --help 查看命令提示");
    }

    public void doExecute(String[] args) {
        commandLine.execute(args);
    }

}
