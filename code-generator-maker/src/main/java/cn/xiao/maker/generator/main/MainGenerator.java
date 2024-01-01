package cn.xiao.maker.generator.main;

/**
 * 生成模板子类
 *
 * @author xiao
 */
public class MainGenerator extends GenerateTemplate {

    @Override
    protected void buildDist(String outputPath, String jarName, String sourceCopyDestPath) {
        System.out.println("取消构建精简包...");
    }
}
