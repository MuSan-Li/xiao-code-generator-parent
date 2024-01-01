// package cn.xiao;
//
// import cn.xiao.maker.model.DataModel;
// import lombok.Data;
// import picocli.CommandLine;
// import picocli.CommandLine.Command;
// import picocli.CommandLine.Option;
//
// @Command(name = "test", mixinStandardHelpOptions = true)
// @Data
// public class TestGroupCommad implements Runnable {
//
//     @Option(names = {"-n", "--needGit"}, arity = "0..1", description = "是否生成 .gitignore文件", interactive = true, echo = true)
//     private boolean needGit = true;
//
//     @Option(names = {"-l", "--loop"}, arity = "0..1", description = "是否生成循环", interactive = true, echo = true)
//     private boolean loop = false;
//
//     static DataModel.MainTemplate mainTemplate = new DataModel.MainTemplate();
//
//     // run方法打印属性值
//     @Override
//     public void run() {
//         System.out.println(needGit);
//         System.out.println(loop);
//         if (loop) {
//             CommandLine commandLine = new CommandLine(MainTemplate.class);
//             commandLine.execute("-a", "-o");
//             System.out.println(mainTemplate);
//         }
//     }
//
//     // 静态内部类，定义 mainTemplate.author 和 mainTemplate.outputText 属性
//     @Data
//     static class MainTemplate implements Runnable {
//         @Option(names = {"-a", "--author"}, arity = "0..1", description = "作者注释", interactive = true, echo = true)
//         private String author = "xiao";
//
//         @Option(names = {"-o", "--outputText"}, arity = "0..1", description = "输出信息", interactive = true, echo = true)
//         private String outputText = "sum = ";
//
//         @Override
//         public void run() {
//             mainTemplate.setAuthor(author);
//             mainTemplate.setOutputText(outputText);
//         }
//     }
//
//     // main方法写调用逻辑
//     public static void main(String[] args) {
//         CommandLine commandLine = new CommandLine(TestGroupCommad.class);
//         commandLine.execute("-n", "-l");
//     }
// }