package cn.xiao.maker.generator.main;

/**
 * 生成模板子类
 *
 * @author xiao
 */
public class MainGenerator extends GenerateTemplate {

    @Override
    protected String buildDist(String outputPath, String jarName, String sourceCopyDestPath) {
        System.out.println("不构建精简包...");
        return "";
    }
}
