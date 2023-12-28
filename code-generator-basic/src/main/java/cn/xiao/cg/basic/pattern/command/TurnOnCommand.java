package cn.xiao.cg.basic.pattern.command;

/**
 * 开机命令
 *
 * @author xiao
 */
public class TurnOnCommand implements Command {

    private Driver driver;

    public TurnOnCommand(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void execute() {
        driver.turnOn();
    }
}
