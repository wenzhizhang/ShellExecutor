package com.duanxian.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by yanbizha on 2017/8/31.
 */
public class BatchShellExecutor
{

    private static final int POOL_SIZE = Config.getInt("POOL_SIZE", 30);
    private static final int TIMEOUT = 120;
    private static final Logger LOGGER = LogManager.getLogger(BatchShellExecutor.class);
    private static final ExitCode EXIT_CODE = new ExitCode();
    private List<ShellFeedback> succList = new ArrayList<>();
    private List<ShellFeedback> failedList = new ArrayList<>();

    public List<ShellFeedback> getSuccList()
    {
        return succList;
    }

    public void setSuccList(List<ShellFeedback> succList)
    {
        this.succList = succList;
    }

    public List<ShellFeedback> getFailedList()
    {
        return failedList;
    }

    public void setFailedList(List<ShellFeedback> failedList)
    {
        this.failedList = failedList;
    }

    public void executeCommands(List<String> commandList)
    {
        LOGGER.debug("Pool size is: " + POOL_SIZE);
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);
        CompletionService<ShellFeedback> completionService = new ExecutorCompletionService<>(executorService);
        for (String command : commandList)
        {
            LOGGER.debug("Starting new thread for command execution: " + command);
            ShellExecutorThread executorThread = new ShellExecutorThread(command);
            completionService.submit(executorThread);
        }
        int activeThreads = Thread.activeCount();
        LOGGER.debug("Active threads: " + activeThreads);
        for (int i = 0; i < commandList.size(); i++)
        {
            Future<ShellFeedback> future = null;
            String command = null;
            ShellFeedback feedback = null;
            try
            {
                future = completionService.take();
                feedback = future.get(TIMEOUT, TimeUnit.SECONDS);
                int exitCode = feedback.getExitCode();
//                command = feedback.getCommand();
//                LOGGER.info("Exit code is: " + exitCode);
//                LOGGER.info("Command execution result is: " + EXIT_CODE.getResultByExitCode(exitCode));
                long timeCost = feedback.getProcTime();
                LOGGER.debug("Time cost: " + timeCost + " ms");
                if (exitCode == 0)
                {
                    addToSuccList(feedback);
                }
                else
                {
                    addToFailedList(feedback);
                }
            }
            catch (InterruptedException e)
            {
                LOGGER.error(e.getMessage());
                future.cancel(true);
                addToFailedList(feedback);
            }
            catch (ExecutionException e)
            {
                LOGGER.error(e.getMessage());
                future.cancel(true);
                addToFailedList(feedback);
            }
            catch (TimeoutException e)
            {
                LOGGER.error("Shell execution failed due to timeout.");
                future.cancel(true);
                addToFailedList(feedback);
            }
        }
        executorService.shutdown();
        long totalTime = System.currentTimeMillis() - startTime;
        LOGGER.info("Total time cost is: " + totalTime + " ms");
    }

    private synchronized void addToSuccList(ShellFeedback shellFeedback)
    {
        succList.add(shellFeedback);
    }

    private synchronized void addToFailedList(ShellFeedback shellFeedback)
    {
        failedList.add(shellFeedback);
    }

}
