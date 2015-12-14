/*
        Copyright (c) 2012, 2013, 2014, Groupon, Inc.
        All rights reserved.

        Redistribution and use in source and binary forms, with or without
        modification, are permitted provided that the following conditions
        are met:

        Redistributions of source code must retain the above copyright notice,
        this list of conditions and the following disclaimer.

        Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.

        Neither the name of GROUPON nor the names of its contributors may be
        used to endorse or promote products derived from this software without
        specific prior written permission.

        THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
        IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
        TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
        PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
        HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
        SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
        TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
        PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
        LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
        NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
        SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.groupon.roboremote.roboremoteclientcommon;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Map;

public class Utils {
    public static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static ArrayList<String> jsonArrayToStringList(JSONArray arry) throws Exception {
        ArrayList<String> newArray = new ArrayList<String>();
        for (int x = 0; x < arry.length(); x++) {
            newArray.add(arry.getString(x));
        }

        return newArray;
    }

    /**
     * returns values for a key in the following order:
     * 1. First checks environment variables
     * 2. Falls back to system properties
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public static String getEnv(String name, String defaultValue) {
        Map<String, String> env = System.getenv();

        // try to get value from environment variables
        if (env.get(name) != null) {
            return env.get(name);
        }

        // fall back to system properties
        return System.getProperty(name, defaultValue);
    }

    public static int getFreePort() throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        serverSocket.close();

        return port;
    }

    public static String executeLocalCommand(String[] command) {
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            reader =
                    new BufferedReader(new InputStreamReader(p.getErrorStream()));

            line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    public static void clearStaleADBTunnels(String type) throws Exception {
        // looks for PID style files with the specified type, removes tunnels and deletes files
        String lsResult = executeLocalCommand(new String[] {"adb", "-s", DebugBridge.get().getSerialNumber(), "shell", "ls", "/data/local/tmp"});
        String[] lsResults = lsResult.split(System.getProperty("line.separator"));
        for (String file : lsResults) {
            if (file.startsWith(type + "_PORT_")) {
                String portStr = file.replace(type + "_PORT_", "");
                int port = Integer.parseInt(portStr);
                try {
                    DebugBridge.get().deleteTunnel(port, port);
                } catch (Exception e) {
                    // ddmlib will throw an exception if the tunnel doesn't exist
                    logger.info("Exception deleting tunnel ignored.  Tunnel may not have existed.");
                }
                DebugBridge.get().runShellCommand("rm /data/local/tmp/" + file);

                logger.info("Removed stale port forward: {}", port);
            }
        }
    }

    public static void addADBTunnelWithPIDFile(String type, int port) throws Exception {
        DebugBridge.get().createTunnel(port, port);
        DebugBridge.get().runShellCommand("touch /data/local/tmp/" + type + "_PORT_" + port);
    }

    public static void deleteDirectory(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                deleteDirectory(c);
            }
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }
}
