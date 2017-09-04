package com.duanxian.shell;

import java.io.*;

public class ShellOutHandler extends Thread {
    InputStream inputStream;
    String type;
    OutputStream outputStream;

    ShellOutHandler(InputStream inputStream, String type) {
        this(inputStream, type, null);
    }

    ShellOutHandler(InputStream inputStream, String type, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.type = type;
        this.outputStream = outputStream;
    }

    public void run() {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;
        try {
            if (outputStream != null)
                printWriter = new PrintWriter(outputStream);

            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line=null;
            while ( (line = bufferedReader.readLine()) != null) {
                if (printWriter != null)
                    printWriter.println(line);
                System.out.println(type + ">" + line);
            }

            if (printWriter != null)
                printWriter.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally{

            try
            {
//                printWriter.close();
                bufferedReader.close();
                inputStreamReader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }
}