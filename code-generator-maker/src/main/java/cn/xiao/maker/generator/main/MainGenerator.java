package cn.xiao.maker.generator.main;

/**
 * 生成器入口
 *
 * @author xiao
 */
public class MainGenerator extends GenerateTemplate {

    @Override
    protected void buildDist(String outputPath, String jarName, String sourceCopyDestPath) {
        System.out.println("取消构建精简包...");
    }

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
