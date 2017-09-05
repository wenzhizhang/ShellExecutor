package com.duanxian.shell;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the methods to execute shell commands / scripts on remote host, multiple threads can be running in parallel.
 * Created by Lionsong on 2017/7/6.
 */
public class RemoteShellExecutor {
    private Session session;
    private String command;
    private String charset;
    private static final Logger LOGGER = LogManager.getLogger(RemoteShellExecutor.class);
    private static final JSch JSCH = new JSch();

    public RemoteShellExecutor(Session session, String command, String charset) {
        this.session = session;
        this.command = command;
        this.charset = charset;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * This method executes shell commands in an active session.
     *
     * @param session
     * @param command
     * @param charset
     * @return
     */
    public List<String> execShellWtihOutput(Session session, String command, String charset) {
        if (session == null) {
            try {
                throw new Exception("Session is null.");
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                System.exit(1);
            }
        }

        List<String> output = new ArrayList<String>();
        try {
            ChannelExec channel = this.getExecChannel(session, command, charset);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(channel.getInputStream(), Charset.forName(charset)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                output.add(line);
            }
            bufferedReader.close();
            channel.disconnect();

        } catch (JSchException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return output;

    }

    /**
     * This method executes shell command on remote host and gets the exit code of the shell command.
     *
     * @param session
     * @param command
     * @param charset
     * @return
     */
    public int execShellWithResponse(Session session, String command, String charset) {
        int response = 0;
        try {
            ChannelExec channel = this.getExecChannel(session, command, charset);
            if (channel.isClosed()){
                response = channel.getExitStatus();
            }
            channel.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            LOGGER.error(e.getMessage());
        }
        return response;
    }

    public ShellFeedback execShellWithFeedback(Session session, String command, String charset){
        if (session == null) {
            try {
                throw new Exception("Session is null.");
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                System.exit(1);
            }
        }
        int response = 0;
        List<String> output = new ArrayList<String>();
        try {
            ChannelExec channel = this.getExecChannel(session, command, charset);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(channel.getInputStream(), Charset.forName(charset)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                output.add(line);
            }
            if (channel.isClosed()){
                response = channel.getExitStatus();
            }
            bufferedReader.close();
            channel.disconnect();

        } catch (JSchException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        ShellFeedback shellFeedback = new ShellFeedback(command,output, response);
        return shellFeedback;

    }

    private ChannelExec getExecChannel(Session session, String command, String charset) throws JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);
        channel.connect();
        LOGGER.debug("Channel is connected.");
        return channel;
    }


}
