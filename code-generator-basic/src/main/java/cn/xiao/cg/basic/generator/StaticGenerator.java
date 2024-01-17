package cn.xiao.cg.basic.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * 静态生成
 *
 * @author xiao
 */
public class StaticGenerator {

    private StaticGenerator() {

    }

    /**
     * main
     *
     * @param args
     */
    public static void main(String[] args) throws FileNotFoundException {
        String projectProperty = System.getProperty("user.dir");
        System.out.println(projectProperty);
        String srcPath = new File(projectProperty).getParent() + File.separator + "code-generator-template" + File.separator + "acm-template";
        String destPath = projectProperty + File.separator + "templateOutput";
        // copyFileByHutool(srcPath, projectProperty);
        copyFileByRecursion(srcPath, destPath);

    }


    /**
     * 使用hutools工具类复制文件
     *
     * @param srcPath  源路径
     * @param destPath 目标路径
     */
    public static void copyFileByHutool(String srcPath, String destPath) {
        FileUtil.copy(srcPath, destPath, false);
        System.out.println("==== generator static file success ====");
    }


    /**
     * 使用递归复制文件
     *
     * @param srcPath  源路径
     * @param destPath 目标路径
     */
    public static void copyFileByRecursion(String srcPath, String destPath) throws FileNotFoundException {
        // 是否全部为空
        if (CharSequenceUtil.hasBlank(srcPath, destPath)) {
            throw new RuntimeException("srcPath or destPath is null");
        }
        // 是否路径相同
        if (Objects.equals(srcPath, destPath)) {
            throw new RuntimeException("srcPath and destPath is same");
        }
        if (!FileUtil.exist(srcPath)) {
            FileUtil.mkdir(srcPath);
        }
        // 源文件路径是否存在
        File file = new File(srcPath);
        System.out.println(file);
        if (!file.exists()) {
            throw new RuntimeException("srcPath is not exist");
        }
        recursionCopyFile(file, destPath);
        System.out.println("==== generator static file success ====");
    }


    /**
     * 递归复制文件
     *
     * @param srcPathFile 源路径文件
     * @param destPath    目标路径
     */
    private static void recursionCopyFile(File srcPathFile, String destPath) throws FileNotFoundException {
        // 文件不存在则创建
        if (!srcPathFile.isDirectory()) {
            File destFile = new File(destPath + File.separator + srcPathFile.getName());
            try {
                // StandardCopyOption.REPLACE_EXISTING 文件存在则覆盖
                Files.copy(srcPathFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            // 文件夹不存在则创建
            String name = srcPathFile.getName();
            File file = new File(destPath + File.separator + name);
            if (!file.exists()) {
                file.mkdir();
            }
            String[] list = srcPathFile.list();
            for (String childrenFilePath : list) {
                File childrenFile = new File(srcPathFile + File.separator + childrenFilePath);
                recursionCopyFile(childrenFile, file.getPath());
            }
        }
    }



    /*
    Files.copy()方法是Java中的一个API，用于将源文件或目录复制到目标文件或目录。它是在Java 7中引入的。

    该方法的语法为：
    Files.copy(Path source, Path target, CopyOption... options)

    其中，source参数是要复制的源文件或目录的路径；target参数是复制后的目标文件或目录的路径；options参数是一个可选的参数，用于指定复制选项。

    该方法的工作原理如下：

    如果源文件是一个目录，则递归地复制整个目录树。
    如果目标文件已存在，则抛出FileAlreadyExistsException异常。
    如果目标文件所在的目录不存在，则会创建所需的目录。
    如果源文件是一个符号链接，则复制链接本身，而不是链接的目标文件。
    此外，还可以通过指定CopyOption来控制复制操作的行为，包括：

    StandardCopyOption.REPLACE_EXISTING：如果目标文件已存在，则替换它。
    StandardCopyOption.COPY_ATTRIBUTES：同时复制文件的属性，如文件权限、时间戳等。
    LinkOption.NOFOLLOW_LINKS：不复制符号链接。
    需要注意的是，Files.copy()方法在复制大文件或目录时可能会占用较多的内存，因此建议使用Files.copy(InputStream, Path, CopyOption...)或Files.copy(Path, OutputStream)这些适用于复制大文件的API来处理大文件的复制操作。
     */
}