package com.eu.cloud.nacos;

import java.io.IOException;
import java.io.InputStream;

public class RegisterConfigShutDown {

    public static void main(String[] args) {
        String osName = System.getProperty("os.name");
        String resource = RegisterConfigShutDown.class.getResource("").toString().replace("target/classes", "src/main/java");
        String cmd = null;

        if (osName.contains("Mac") || osName.contains("Linux")) {
            cmd = "sh " + resource.replace("file:", "") + "bin/shutdown.sh";
        } else if (osName.contains("Windows")) {
            cmd = "cmd.exe /c " + resource.replace("file:/", "") + "bin/shutdown.cmd";
        } else {
            System.exit(1);
        }

        InputStream inputStream = null;
        try {
            Process child = Runtime.getRuntime().exec(cmd);
            inputStream = child.getInputStream();
            int c;
            while ((c = inputStream.read()) != -1) {
                System.out.print((char) c);
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
