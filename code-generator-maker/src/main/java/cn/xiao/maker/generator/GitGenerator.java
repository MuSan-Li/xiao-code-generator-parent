package cn.xiao.maker.generator;

import cn.hutool.core.text.StrPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 执行 git 初始化命令
 *
 * @author xiao
 */
public class GitGenerator {

    private GitGenerator() {
    }


    public static void doGenerate(String projectDir) throws Exception {
        // win
        String winMvnCommand = "git.cmd init";
        List<String> commandList = new ArrayList<>();
        commandList.add("git.cmd init");
        commandList.add("git.cmd commit -m \"first commit\"");
        for (String command : commandList) {
            doProcess(projectDir, command);
        }
    }

    private static void doProcess(String projectDir, String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(String.valueOf(StrPool.C_SPACE)));
        processBuilder.directory(new File(projectDir));
        Map<String, String> environment = processBuilder.environment();
        System.out.println(environment);
        Process process = processBuilder.start();
        // 读取命令输出
        InputStream inputStream = process.getInputStream();
        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
        String line;
        while ((line = bf.readLine()) != null) {
            System.out.println(line);
        }
        bf.close();
        inputStream.close();
        int exitCode = process.waitFor();
        System.out.println("Exited with " + exitCode);
    }
}
