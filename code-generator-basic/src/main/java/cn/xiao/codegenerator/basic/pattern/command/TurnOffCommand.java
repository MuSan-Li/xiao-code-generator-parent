package cn.xiao.codegenerator.basic.pattern.command;

/**
 * 关机命令
 *
 * @author xiao
 */
public class TurnOffCommand implements Command {
    private Driver driver;

    public TurnOffCommand(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void execute() {
        driver.turnOff();
    }
}
