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

import com.google.common.io.Files;
import com.groupon.roboremote.roboremoteclientcommon.logging.TestLogger;

import java.io.*;

/**
 * This class defines general device/emulator commands
 */
public class Device {
    static boolean emulator = false;

    static String current_log_dir = null;

    public static boolean isEmulator() throws Exception {
        return DebugBridge.get().isEmulator();
    }

    public static void clearAppData(String packageName) throws Exception {
        // clear app data
        DebugBridge.get().runShellCommand("pm clear " + packageName);
    }

    static void delete(File f) throws IOException {
      if (f.isDirectory()) {
        for (File c : f.listFiles()) {
          delete(c);
        }
      }
      if (!f.delete())
        throw new FileNotFoundException("Failed to delete file: " + f);
    }

    /**
     * Stores the specified log for this test
     * @throws Exception
     */
    public static void storeLogs(String sourceLogFileName, String destLogFileName) throws Exception {
        // assumes eventmanager is running
        // store logs
        String tmpdir = System.getProperty("java.io.tmpdir");
        if (tmpdir == null || tmpdir == "null") {
            tmpdir = "/tmp";
        }
    	File tmpLogFile = new File(tmpdir + File.separator + sourceLogFileName);
    	File destFile = new File(current_log_dir + File.separator + destLogFileName);
    	
        Files.copy(tmpLogFile, destFile);
    }

    public static void storeFailurePng() throws Exception {
    	File failureFile = new File("FAILURE.png");
    	File destFile = new File(current_log_dir + File.separator + "FAILURE.png");
    	
    	Files.copy(failureFile, destFile);
    }

    public static void setupLogDirectories(String testName) throws Exception {
        String currentDir = new File("").getAbsolutePath();

        // clear the final log directory
        File log_dir = new File(currentDir + File.separator + "logs" 
              + File.separator + testName + File.separator + "test.log");
        TestLogger.get().info("Log directory: {}", log_dir.getParent());
        
        Files.createParentDirs(log_dir);

        // clear existing files from this location
        if (log_dir.exists()) {
            delete(log_dir);
        }

        current_log_dir = log_dir.getParent();
    }
}
