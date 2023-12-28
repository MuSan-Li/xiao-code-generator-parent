package cn.xiao.maker.cli.command;


import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

/**
 * 查看文件列表
 *
 * @author xiao
 */
@CommandLine.Command(name = "list", description = "查看文件列表", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {

    @Override
    public void run() {
        //当前根路径
        String projectPath = System.getProperty("user.dir");
        String path = new File(String.format("%s%stemplateOutput%sacm-template", projectPath, File.separator, File.separator)).getAbsolutePath();
        List<File> files = FileUtil.loopFiles(path);
        files.forEach(System.out::println);
    }
}
