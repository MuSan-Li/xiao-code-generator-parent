package ${basePackage}.cli.command;


import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

/**
 * 查看文件列表
 *
 * @author ${author}
 */
@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {

    @Override
    public void run() {
        //当前根路径
        String path = "${fileConfig.inputRootPath}";
        List<File> files = FileUtil.loopFiles(path);
        files.forEach(System.out::println);
    }
}
