package cn.xiao.maker.generator.main;

/**
 * 生成模板子类 制作压缩包 不影响主流程 开闭原则
 * 注意：继承GenerateTemplate 重写buildDist方法
 *
 * @author xiao
 */
public class ZipGenerator extends GenerateTemplate {

    @Override
    protected String buildDist(String outputPath, String jarName, String sourceCopyDestPath) {
        String buildDist = super.buildDist(outputPath, jarName, sourceCopyDestPath);
        return super.buildZipPackage(buildDist);
    }
}
