package com.duanxian.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

/**
 * Created by yanbizha on 2017/8/31.
 */
public class ShellExecutorTest
{
    private static final int POOL_SIZE = 20;
    private static final int TIMEOUT = 60;
    private static final Logger LOGGER = LogManager.getLogger(ShellExecutorTest.class);


    public static void main(String[] args){
        long startTime = System.currentTimeMillis();
        String command = args[0];
        ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);
        CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(executorService);
        ShellExecutorThread executorThread = new ShellExecutorThread(command);
        completionService.submit(executorThread);
        Future<Integer> future = null;
        try
        {
            future = completionService.take();
            Integer exitCode = future.get(TIMEOUT, TimeUnit.SECONDS);
            LOGGER.info("Exit code is: "+exitCode);
            long timeCost = System.currentTimeMillis() - startTime;
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
}
