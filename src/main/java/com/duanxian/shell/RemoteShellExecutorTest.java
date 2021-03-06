package com.duanxian.shell;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Lionsong on 2017/7/6.
 */
public class RemoteShellExecutorTest
{
    private static final Logger LOGGER = LogManager.getLogger(RemoteShellExecutorTest.class);
    private static final int POOL_SIZE = 10;
    private static final int TIMEOUT = 60;

    public static void main(String[] args)
    {
        String username = "root";
        String password = "arthur";
        String host = "10.91.51.34";
        String shell = "/opt/oss/NSN-ne3sws_core/install/bin/NE3SWSReregistration.sh ";
        String charset = "UTF-8";
        int port = 22;
        List<String> agents = new ArrayList<>();
        agents.add("PLMN-PLMN/SBTS-1");
        agents.add("PLMN-PLMN/SBTS-2");
        agents.add("PLMN-PLMN/SBTS-3");
        agents.add("PLMN-PLMN/SBTS-4");
        agents.add("PLMN-PLMN/SBTS-6");
        agents.add("PLMN-PLMN/SBTS-7");
        agents.add("PLMN-PLMN/SBTS-8");
        agents.add("PLMN-PLMN/SBTS-9");
        agents.add("PLMN-PLMN/SBTS-10");
        agents.add("PLMN-PLMN/SBTS-11");
        agents.add("PLMN-PLMN/SBTS-12");
        agents.add("PLMN-PLMN/SBTS-13");
        agents.add("PLMN-PLMN/SBTS-14");
        agents.add("PLMN-PLMN/SBTS-15");
        agents.add("PLMN-PLMN/SBTS-16");
        agents.add("PLMN-PLMN/SBTS-17");
        agents.add("PLMN-PLMN/SBTS-18");
        agents.add("PLMN-PLMN/SBTS-19");
        agents.add("PLMN-PLMN/SBTS-20");
        try
        {

            Session session = createSession(username, password, host, port);
            long startTime = System.currentTimeMillis();
            ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);
            CompletionService<ShellFeedback> cs = new ExecutorCompletionService<>(executorService);

            for (String agent : agents)
            {
                LOGGER.debug("Start re-registration for " + agent);
                String cmd = shell + agent.trim();
                RemoteShellExecutorThread rset = new RemoteShellExecutorThread(session, cmd, charset);
                cs.submit(rset);
                LOGGER.debug("Submit re-registration task for " + agent);
            }
            for (int i = 0; i < agents.size(); i++)
            {
                Future<ShellFeedback> future = cs.take();
                ShellFeedback sf = future.get(TIMEOUT, TimeUnit.SECONDS);
                long timeCost = System.currentTimeMillis() - startTime;
                String[] firstLine = sf.getOutput().get(0).split("\\.\\.\\.")[0].split(" ");
                String agent = firstLine[firstLine.length - 1];
                LOGGER.info(sf.getExitCode());
                for (int j = 0; j < sf.getOutput().size(); j++)
                {
                    LOGGER.info(sf.getOutput().get(j));
                }
                LOGGER.info("Time cost for " + agent + ": " + timeCost + " ms.");
            }
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            LOGGER.info("Total time: " + totalTime);
            System.exit(0);

        }
        catch (JSchException e)
        {
            LOGGER.error(e.getMessage());
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
            LOGGER.error(e.getMessage());
        }


    }

    private static Session createSession(String username, String password, String host, int port) throws JSchException
    {
        int timeout = 30000;
        JSch jSch = new JSch();
        Session session = jSch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setTimeout(timeout);
        session.connect();
        return session;
    }
}
