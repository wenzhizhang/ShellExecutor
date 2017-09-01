package com.duanxian.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by yanbizha on 2017/8/31.
 */
public class BatchShellExecutor
{
    private static final int POOL_SIZE = 20;
    private static final int TIMEOUT = 60;
    private static final Logger LOGGER = LogManager.getLogger(BatchShellExecutor.class);

    public void executeCommands(List<String> commandList)
    {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);
        CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(executorService);
        for (String command : commandList)
        {
            long threadStartTime = System.currentTimeMillis();
            ShellExecutorThread executorThread = new ShellExecutorThread(command);
            completionService.submit(executorThread);
            Future<Integer> future;
            try
            {
                future = completionService.take();
                Integer exitCode = future.get(TIMEOUT, TimeUnit.SECONDS);
                LOGGER.info("Exit code is: " + exitCode);
                long timeCost = System.currentTimeMillis() - threadStartTime;
                LOGGER.info("Time cost: " + timeCost + " ms.");
            }
            catch (InterruptedException e)
            {
                LOGGER.error(e.getMessage());
            }
            catch (ExecutionException e)
            {
                LOGGER.error(e.getMessage());
            }
            catch (TimeoutException e)
            {
                LOGGER.error("Shell execution failed due to timeout.");
            }
        }
        long totalTime = System.currentTimeMillis() - startTime;
        LOGGER.info("Total time cost is: " + totalTime);
    }
}
