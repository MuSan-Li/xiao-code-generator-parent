package cn.xiao.codegenerator.basic.pattern.command;

/**
 * 命令模式 测试类
 *
 * @author xiao
 */
public class Client {

    /**
     * main
     *
     * @param args
     */
    public static void main(String[] args) {
        Driver driver1 = new Driver("TV");
        Driver driver2 = new Driver("panda");

        TurnOffCommand offCommand = new TurnOffCommand(driver1);
        TurnOnCommand onCommand = new TurnOnCommand(driver2);

        RemoteControl remoteControl = new RemoteControl();

        remoteControl.setCommand(offCommand);
        remoteControl.pressButton();

        remoteControl.setCommand(onCommand);
        remoteControl.pressButton();
    }
}
