package com.duanxian.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lionsong on 2017/7/6.
 */
public class ShellExecutor
{
    private static final Logger LOGGER = LogManager.getLogger(ShellExecutor.class);
    String command;

    public ShellExecutor(String command){
        this.command = command;
    }

    public int executeShellWithExitCode()
    {
        String cmd = "sh " + this.command;
        try
        {
            Process proc = Runtime.getRuntime().exec(cmd);
            int exitCode = proc.waitFor();
            return exitCode;
        }
        catch (IOException e)
        {
            LOGGER.error(e.getMessage());
        }
        catch (InterruptedException e)
        {
            LOGGER.error(e.getMessage());
        }
        return 5;
    }

    public ShellFeedback executeShellWithFeedback(){
        String cmd = "sh " + this.command;
        List<String> output = new ArrayList<>();
        int exitCode = 0;
        try
        {
            Process process = Runtime.getRuntime().exec(cmd);
            exitCode = process.waitFor();
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null){
                output.add(line);
            }
        }
        catch (IOException e)
        {
           LOGGER.error(e.getMessage());
        }
        catch (InterruptedException e)
        {
           LOGGER.error(e.getMessage());
        }
        return new ShellFeedback(output, exitCode);
    }

}
