package com.duanxian.shell;

import java.util.List;

/**
 * Created by yanbizha on 2017/7/9.
 */
public class ShellFeedback
{
    private String command;
    private List<String> output;
    private List<String> error;
    private long procTime;
    private int exitCode;

    public ShellFeedback(String command, List<String> output, int exitCode, long procTime)
    {
        this.command = command;
        this.output = output;
        this.exitCode = exitCode;
        this.procTime = procTime;
    }

    public long getProcTime()
    {
        return procTime;
    }

    public void setProcTime(long procTime)
    {
        this.procTime = procTime;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public List<String> getOutput()
    {
        return output;
    }

    public void setOutput(List<String> output)
    {
        this.output = output;
    }

    public List<String> getError()
    {
        return error;
    }

    public void setError(List<String> error)
    {
        this.error = error;
    }

    public int getExitCode()
    {
        return exitCode;
    }

    public void setExitCode(int exitCode)
    {
        this.exitCode = exitCode;
    }
}
