package cn.xiao.cg.basic.pattern.command;

/**
 * 遥控器
 *
 * @author xiao
 */
public class RemoteControl {

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    /**
     * 按下按钮 执行命令
     */
    public void pressButton() {
        command.execute();
    }

}
