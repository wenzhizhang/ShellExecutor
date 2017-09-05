package com.duanxian.shell;

import java.util.concurrent.Callable;

/**
 * Created by yanbizha on 2017/8/31.
 */
public class ShellExecutorThread implements Callable
{
    private ShellExecutor shellExecutor;

    public ShellExecutorThread(String command){
        this.shellExecutor = new ShellExecutor(command);
    }

    @Override
    public Object call() throws Exception
    {
        ShellFeedback feedback = this.shellExecutor.executeShellWithFeedback();
        return feedback;
    }
}
