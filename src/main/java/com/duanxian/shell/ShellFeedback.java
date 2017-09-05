package com.duanxian.shell;

import java.util.List;

/**
 * Created by yanbizha on 2017/7/9.
 */
public class ShellFeedback {
    private String command;
    private List<String> output;
    private List<String> error;

    private int exitCode;

    public ShellFeedback(String command, List<String> output, int exitCode) {
        this.command = command;
        this.output = output;
        this.exitCode = exitCode;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getOutput() {
        return output;
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }
}
