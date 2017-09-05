package com.duanxian.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lionsong on 2017/7/6.
 */
public class ShellExecutor
{
    private static final Logger LOGGER = LogManager.getLogger(ShellExecutor.class);
    private String command;

    public ShellExecutor(String command)
    {
        this.command = command;
    }

    public int executeShellWithExitCode()
    {
        String cmd = constructCMD();
        try
        {
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();
            return exitCode;
        }
        catch (IOException e)
        {
            LOGGER.error(e.getMessage());
            return 3;
        }
        catch (InterruptedException e)
        {
            LOGGER.error(e.getMessage());
            return 4;
        }
    }

    public ShellFeedback executeShellWithFeedback()
    {
        String cmd = constructCMD();
        List<String> output = new ArrayList<>();
        int exitCode = 0;
        long startTime = System.currentTimeMillis();
        try
        {
//            LOGGER.debug("Starting to execute command: " + cmd + ", start time: " + startTime);
            Process process = Runtime.getRuntime().exec(cmd);
            getOutput(output, process.getInputStream());
            getOutput(output, process.getErrorStream());
            exitCode = process.waitFor();
        }
        catch (IOException e)
        {
            LOGGER.error(e.getMessage());
        }
        catch (InterruptedException e)
        {
            LOGGER.error(e.getMessage());
        }
        long endTime = System.currentTimeMillis();
//        LOGGER.debug("End of command execution: " + cmd + ", end time: " + endTime);
        long procTime = endTime - startTime;
        return new ShellFeedback(this.command, output, exitCode, procTime);
    }

    private String constructCMD()
    {
        String cmd;
        String main = this.command.split(" ")[0];
        File f = new File(main);
        if (f.exists())
        {
            LOGGER.debug("The command is a shell script.");
            cmd = "sh " + this.command;
        }
        else
        {
            LOGGER.debug("The command is a shell command.");
            cmd = this.command;
        }
        return cmd;
    }

    private void getOutput(List<String> output, InputStream inputStream)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                try
                {
                    while ((line = br.readLine()) != null)
                    {
                        output.add(line);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
