/*
 * Copyright (c) 2012, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.</div>
 */

package com.groupon.roboremote.roboremoteclient;

import com.google.common.io.Files;
import com.groupon.roboremote.roboremoteclient.logging.TestLogger;

import java.io.*;

/**
 * This class defines general device/emulator commands
 */
public class Device {
    static String app_package = null;
    static String test_class = null;
    static String test_runner = null;
    static boolean emulator = false;

    static String current_log_dir = null;

    static AppThread ap = null;

    public static boolean isEmulator() throws Exception {
        return DebugBridge.get().isEmulator();
    }

    public static void setAppEnvironmentVariables(String appPackage, String testClass, String testRunner) {
        app_package = appPackage;
        test_class = testClass;
        test_runner = testRunner;
    }

    public static void setAppEnvironmentVariables() throws Exception {
        // get environment variables
        app_package = Utils.getEnv("ROBO_APP_PACKAGE", app_package);
        if (app_package == null) {
            throw new Exception("ROBO_APP_PACKAGE is not set");
        }

        test_class = Utils.getEnv("ROBO_TEST_CLASS", test_class);
        if (test_class == null) {
            throw new Exception("ROBO_TEST_CLASS is not set");
        }

        test_runner = Utils.getEnv("ROBO_TEST_RUNNER", test_runner);
        if (test_runner == null) {
            throw new Exception("ROBO_TEST_RUNNER is not set");
        }
    }

    public static void clearAppData() throws Exception {
        // clear app data
        DebugBridge.get().runShellCommand("pm clear " + app_package);
    }
    
    public static String getAppPackage() {
        return app_package;
    }
    
    public static String getTestClass() {
        return test_class;
    }
    
    public static String getTestRunner() {
        return test_runner;
    }

    static void delete(File f) throws IOException {
      if (f.isDirectory()) {
        for (File c : f.listFiles())
          delete(c);
      }
      if (!f.delete())
        throw new FileNotFoundException("Failed to delete file: " + f);
    }

    public static void storeLogs() throws Exception {
        // assumes eventmanager is running
        // store logs
    	File tmpLogFile = new File(System.getProperty("java.io.tmpdir") + File.pathSeparator + "adb_robo.log");
    	File destFile = new File(current_log_dir + File.pathSeparator + "test.log");
    	
        Files.copy(tmpLogFile, destFile);
    }

    public static void storeFailurePng() throws Exception {
    	File failureFile = new File("FAILURE.png");
    	File destFile = new File(current_log_dir);
    	
    	Files.copy(failureFile, destFile);
    }

    public static void setupLogDirectories() throws Exception {
        String currentDir = new File("").getAbsolutePath();

        // clear the final log directory
        File log_dir = new File(currentDir + File.pathSeparator + "logs" 
              + File.pathSeparator + Utils.getTestName());
        TestLogger.get().info("Log directory: {}", log_dir.getAbsolutePath());
        
        log_dir.mkdirs();

        // clear existing files from this location
        if (log_dir.exists()) {
            delete(log_dir);
        }

        current_log_dir = log_dir.getAbsolutePath();
    }

    public static void startApp() throws Exception {
        ap = new AppThread();
        ap.start();

        for (int x = 0; x < 10; x++) {
            // try to make a query.. if it doesnt work then sleep
            TestLogger.get().info("Trying to ping test server..");
            if (Client.isListening())
                break;

            if (x == 9)
                throw new Exception("Could not contact test server");
            
            Thread.sleep(5000);
        }
    }
    
    public static void killApp() throws Exception {
        // try to kill just by calling exit
        try {
            Client.map("java.lang.System", "exit", 0);
        } catch (Exception e) {
            // this will actually throw an exception since it doesnt get a response from this command
        }

        // shut down the thread
        if (ap != null) {
            ap.close();
            ap.interrupt();
            ap = null;
        }

        // wait for the server to be dead
        for (int x = 0; x < 10; x++) {
            // try to make a query.. if it doesnt work then sleep
            TestLogger.get().info("Trying to see if server is still available..");

            if (! Client.isListening())
                break;

            if (x == 9)
                throw new Exception("Server is still available, but should not be");

            Thread.sleep(2000);
        }
    }

    /**
     * This thread contains the running RC test
     * DebugBridge does not return until the instrumentation finishes so we have to run it in its own thread
     */
    private static class AppThread extends Thread {
        DebugBridge.MultiReceiver _receiver = null;

        public void run() {
            _receiver = new DebugBridge.MultiReceiver();
            try {
                DebugBridge.get().runShellCommand("am instrument -e class "  + Device.getTestClass() + " -w " + Device.getTestRunner(), _receiver, 0);
            } catch (Exception e) {
                
            }
        }

        public void close() {
            // close the receiver to kill the thread
            _receiver.close();
        }
    }
}
