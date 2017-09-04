package com.duanxian.shell;

/**
 * Created by yanbizha on 2017/9/1.
 */
public class ExitCode
{
    private static final String SUCCEEDED = "Re-registration is finished successfully.";
    private static final String UNREG_FAIL = "Un-registration is failed.";
    private static final String REG_FAIL = "Registration is failed.";
    private static final String UNKNOWN = "Unknown exit code.";

    public String getResultByExitCode(int exitCode){
        String result;
        switch (exitCode){
            case 0:
                result = SUCCEEDED;
                break;
            case 1:
                result = UNREG_FAIL;
                break;
            case 2:
                result = REG_FAIL;
                break;
            default:
                result = UNKNOWN;
        }
        return result;
    }
}
