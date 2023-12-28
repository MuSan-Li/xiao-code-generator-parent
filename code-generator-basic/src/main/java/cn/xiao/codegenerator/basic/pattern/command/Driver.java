package cn.xiao.codegenerator.basic.pattern.command;

/**
 * 接收者 电视机 有两个命令，一个开机，一个关机
 *
 * @author xiao
 */
public class Driver {

    private String name;

    public Driver(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * 开机
     */
    public void turnOn() {
        System.out.println(name + "开机");
    }

    /**
     * 关机
     */
    public void turnOff() {
        System.out.println(name + "关机");
    }

}
