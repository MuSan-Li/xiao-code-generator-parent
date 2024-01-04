package cn.xiao.file;

import java.io.File;

public class DelFile {

    public static void main(String[] args) {
        String filePath = "D:\\Project\\xiao-code-generator-parent\\code-generator-web-frontend\\node_modules";
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isDirectory()) {
                // 如果是目录，则递归删除目录下的所有文件和子目录
                deleteSubFiles(file);
            }
            // 删除文件或空目录
            boolean delete = file.delete();
            System.out.println(filePath + " has been deleted.");
        }
    }

    private static void deleteSubFiles(File directory) {
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    deleteSubFiles(file);
                }
                boolean delete = file.delete();
                System.out.println(file.getAbsolutePath() + " has been deleted.");
            }
        }
        boolean delete = directory.delete();
        System.out.println(directory.getAbsolutePath() + " has been deleted.");
    }
}
