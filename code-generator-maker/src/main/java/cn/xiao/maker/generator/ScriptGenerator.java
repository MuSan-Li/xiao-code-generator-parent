package cn.xiao.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * 生成 git初始化 mvn打包 脚本文件
 *
 * @author xiao
 */
public class ScriptGenerator {

    public static void doGenerate(String outputPath, String jarPath) throws IOException {

        String mavenScript = "generator";
        String gitScript = "git-init";
        // 直接写入脚本文件
        // linux
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash").append("\n")
                .append(String.format("java -jar %s \"$@\"", jarPath));
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath + File.separator + mavenScript + ".sh");

        sb = new StringBuilder();
        sb.append("git init").append("\n")
                .append("git add .").append("\n")
                .append("git commit -m \"first commit\"");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath + File.separator + gitScript + ".sh");

        // win 执行可能报错 直接略过
        // linux系统 要给文件赋权限操作 win则不用
        if (!StrUtil.contains(System.getProperty("os.name").toLowerCase(), "win")) {
            // 添加可执行权限
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(Paths.get(outputPath), permissions);
        }

        // win
        sb = new StringBuilder();
        sb.append("@echo off").append("\n").append(String.format("java -jar %s %%*", jarPath));
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath + File.separator + mavenScript + ".bat");

        sb = new StringBuilder();
        sb.append("git init").append("\n")
                .append("git add .").append("\n")
                .append("git commit -m \"first commit\"");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath + File.separator + gitScript + ".bat");
    }


    /**
     * test os info
     *
     * @param args
     */
    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            System.out.println("Windows系统");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            System.out.println("Linux或Mac系统");
        } else {
            System.out.println("其他系统");
        }
    }

}
