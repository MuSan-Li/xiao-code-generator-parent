package cn.xiao.maker;

import cn.xiao.maker.generator.main.GenerateTemplate;
import cn.xiao.maker.generator.main.ZipGenerator;

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
        // GenerateTemplate generator = new MainGenerator();
        GenerateTemplate generator = new ZipGenerator();
        generator.doGenerate();
    }
}