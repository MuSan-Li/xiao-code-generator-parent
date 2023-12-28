package cn.xiao.codegenerator.basic.pattern.command;

public class Client {

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
