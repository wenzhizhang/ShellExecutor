package com.duanxian.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by yanbizha on 2017/9/4.
 */
public class SingleShellExecutorTest
{
    private static final Logger LOGGER = LogManager.getLogger(SingleShellExecutorTest.class);
    private static final String RE_REG = "/opt/oss/NSN-ne3sws_core/install/bin/NE3SWSReregistration.sh ";
    public static void main(String[] args){
        String fqdn = "PLMN-PLMN/SBTS-9";
        String command = RE_REG + fqdn;
        ShellExecutor executor = new ShellExecutor(command);
        int exitCode = executor.executeShellWithExitCode();
        LOGGER.debug("Exit code is: "+exitCode);
    }
}
