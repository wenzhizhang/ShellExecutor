package com.duanxian.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by yanbizha on 2017/8/31.
 */
public class BatchShellExecutor {
    private static final int POOL_SIZE = 20;
    private static final int TIMEOUT = 120;
    private static final Logger LOGGER = LogManager.getLogger(BatchShellExecutor.class);
    private static final ExitCode EXIT_CODE = new ExitCode();
    private List<String> succList = new ArrayList<>();
    private List<String> failedList = new ArrayList<>();

    public List<String> getSuccList() {
        return succList;
    }

    public void setSuccList(List<String> succList) {
        this.succList = succList;
    }

    public List<String> getFailedList() {
        return failedList;
    }

    public void setFailedList(List<String> failedList) {
        this.failedList = failedList;
    }

    public void executeCommands(List<String> commandList) {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);
        CompletionService<ShellFeedback> completionService = new ExecutorCompletionService<>(executorService);
        for (String command : commandList) {
            LOGGER.debug("Starting new thread for command execution: " + command);
            ShellExecutorThread executorThread = new ShellExecutorThread(command);
            completionService.submit(executorThread);
        }
        for (int i = 0; i < commandList.size(); i++) {
            Future<ShellFeedback> future = null;
            String command = null;
            try {
                future = completionService.take();
                ShellFeedback feedback = future.get(TIMEOUT, TimeUnit.SECONDS);
                int exitCode = feedback.getExitCode();
                List<String> output = feedback.getOutput();
                command = feedback.getCommand();
                StringBuilder sb = new StringBuilder();
                for (String line : output) {
                    sb.append(line);
                }
                LOGGER.debug("Output: \n" + sb.toString());
                LOGGER.info("Exit code is: " + exitCode);
                LOGGER.info("Command execution result is: " + EXIT_CODE.getResultByExitCode(exitCode));
                long timeCost = System.currentTimeMillis() - startTime;
                LOGGER.info("Time cost: " + timeCost + " ms.");
                if (exitCode == 0) {
                    addToSuccList(command);
                } else {
                    addToFailedList(command);
                }
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
                future.cancel(true);
                addToFailedList(command);
            } catch (ExecutionException e) {
                LOGGER.error(e.getMessage());
                future.cancel(true);
                addToFailedList(command);
            } catch (TimeoutException e) {
                LOGGER.error("Shell execution failed due to timeout.");
                future.cancel(true);
                addToFailedList(command);
            }
        }
        executorService.shutdown();
        long totalTime = System.currentTimeMillis() - startTime;
        LOGGER.info("Total time cost is: " + totalTime);
        System.exit(0);
    }

    private synchronized void addToSuccList(String command) {
        succList.add(command);
    }

    private synchronized void addToFailedList(String command) {
        failedList.add(command);
    }

}
