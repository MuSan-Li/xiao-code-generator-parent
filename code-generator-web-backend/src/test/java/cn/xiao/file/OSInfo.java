package cn.xiao.file;

public class OSInfo {
    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            System.out.println("当前操作系统是Windows");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            System.out.println("当前操作系统是Linux");
        } else {
            System.out.println("当前操作系统不是Windows也不是Linux");
        }
    }
}