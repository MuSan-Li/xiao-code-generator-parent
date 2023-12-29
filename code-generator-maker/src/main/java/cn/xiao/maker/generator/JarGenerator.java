package cn.xiao.maker.generator;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 * jar 生成
 *
 * @author xiao
 */
public class JarGenerator {

    private JarGenerator() {
    }


    public static void doGenerate(String projectDir) throws Exception {
        // win
        String winMvnCommand = "mvn.cmd clean package -DskipTests=true";
        // other
        // String otherMvnCommand = "mvn clean package -DskipTests=true";

        String mvnCommand = winMvnCommand;
        ProcessBuilder processBuilder = new ProcessBuilder(mvnCommand.split(String.valueOf(StrPool.C_SPACE)));
        processBuilder.directory(new File(projectDir));
        Map<String, String> environment = processBuilder.environment();
        System.out.println(environment);
        Process process = processBuilder.start();

        // 读取命令输出
        InputStream inputStream = process.getInputStream();

        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream,"GBK"));
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
