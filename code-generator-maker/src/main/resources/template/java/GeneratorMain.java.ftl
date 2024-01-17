package ${basePackage};

import ${basePackage}.cli.CommandExecutor;

/**
* main
*
* @author ${author}
*/
public class GeneratorMain {

/**
* main
*
* @param args
*/
public static void main(String[] args) {
CommandExecutor commandExecutor = new CommandExecutor();
commandExecutor.doExecute(args);
}
}