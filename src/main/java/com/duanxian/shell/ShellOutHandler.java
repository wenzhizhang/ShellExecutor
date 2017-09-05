package com.duanxian.shell;

import java.io.*;
import java.util.List;

public class ShellOutHandler extends Thread {
    InputStream inputStream;
    String type;
    List<String> output;

    public ShellOutHandler(InputStream inputStream, String type) {
        this.inputStream = inputStream;
        this.type = type;
    }

    public void run() {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.output.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                bufferedReader.close();
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}