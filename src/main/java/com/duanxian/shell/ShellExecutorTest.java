package com.duanxian.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by yanbizha on 2017/8/31.
 */
public class ShellExecutorTest
{
    private static final int POOL_SIZE = 20;
    private static final int TIMEOUT = 60;
    private static final Logger LOGGER = LogManager.getLogger(ShellExecutorTest.class);
    private static final String TEST = "testShell.sh ";


    public static void main(String[] args){
        List<String> cmdList = new ArrayList<>();
        for (int i=0;i<100;i++){
            String command = TEST + i;
            cmdList.add(command);
        }
        BatchShellExecutor batchShellExecutor = new BatchShellExecutor();
        batchShellExecutor.executeCommands(cmdList);
        List<String> succList = batchShellExecutor.getSuccList();
        List<String> failedList = batchShellExecutor.getFailedList();
        StringBuilder succSB = new StringBuilder();
        for (String line: succList){
            succSB.append(line);
        }
        LOGGER.info("Succeeded List: \n" + succSB.toString());
        StringBuilder failedSB = new StringBuilder();
        for (String line : failedList){
            failedSB.append(line);
        }
        LOGGER.info("Failed List: \n" + failedSB.toString());
        System.exit(0);
    }
}
