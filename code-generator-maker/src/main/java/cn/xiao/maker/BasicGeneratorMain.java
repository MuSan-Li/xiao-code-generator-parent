package cn.xiao.maker;

import cn.xiao.maker.generator.main.MainGenerator;

/**
 * 主入口
 *
 * @author xiao
 */
public class BasicGeneratorMain {


    /**
     * 主函数
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();
    }
}