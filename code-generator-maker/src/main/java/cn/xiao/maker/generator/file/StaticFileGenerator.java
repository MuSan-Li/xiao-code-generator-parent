package cn.xiao.maker.generator.file;

import cn.hutool.core.io.FileUtil;

/**
 * 静态生成
 *
 * @author xiao
 */
public class StaticFileGenerator {

    private StaticFileGenerator() {

    }


    /**
     * 使用hutools工具类复制文件
     *
     * @param srcPath  源路径
     * @param destPath 目标路径
     */
    public static void doGenerate(String srcPath, String destPath) {
        FileUtil.copy(srcPath, destPath, false);
        System.out.println("==== generator static file success ====");
    }
}