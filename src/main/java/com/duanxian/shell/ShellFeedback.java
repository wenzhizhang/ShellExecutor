package com.duanxian.shell;

import java.util.List;

/**
 * Created by yanbizha on 2017/7/9.
 */
public class ShellFeedback
{
    private List<String> output;
    private int exitCode;

    public ShellFeedback(List<String> output, int exitCode){
        this.output = output;
        this.exitCode = exitCode;
    }

    public List<String> getOutput()
    {
        return output;
    }

    public void setOutput(List<String> output)
    {
        this.output = output;
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
