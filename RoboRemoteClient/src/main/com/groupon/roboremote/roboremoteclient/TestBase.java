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

package com.groupon.roboremote.roboremoteclient;

import com.android.ddmlib.NullOutputReceiver;
import com.groupon.roboremote.Constants;
import com.groupon.roboremote.roboremoteclientcommon.DebugBridge;
import com.groupon.roboremote.roboremoteclientcommon.Device;
import com.groupon.roboremote.roboremoteclientcommon.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.groupon.roboremote.roboremoteclientcommon.logging.*;

import java.io.File;
import java.lang.Exception;
import java.lang.String;
import java.lang.Thread;

public class TestBase {
    public static final Logger logger = LoggerFactory.getLogger("test");
    static String app_package = null;
    static String test_class = null;
    static String test_runner = null;
    static AppThread ap = null;
    static int _roboremote_port = Constants.ROBOREMOTE_SERVER_PORT;

    public static void onFailure() throws Exception {
        logger.warn("TestBase::OnFailure:: Taking screenshot");
        DebugBridge.get().getScreenShot("FAILURE.png");
        Device.storeFailurePng();
    }
    
    public static void setUp(String testName) throws Exception {
        setUp(testName, false, true, _roboremote_port);
    }

    public static void setUp(String testName, int port) throws Exception {
        setUp(testName, false, true, port);
    }

    /**
     * Get the port that the automator is running on
     * @return
     */
    public static int getRoboRemotePort() {
        return _roboremote_port;
    }

    /**
     * This is the generic test setup function
     * @param relaunch - true if this is an app relaunch
     * @param clearAppData - true if you want app data cleared, false otherwise
     */
    public static void setUp(String testName, Boolean relaunch, Boolean clearAppData, int port) throws Exception {
        // another port may have been passed in for use
        _roboremote_port = port;

        if (! relaunch) {
            logger.info("Starting test {}", testName);
            Utils.setTestName(testName);
            Device.setupLogDirectories();

            // create adb tunnel
            DebugBridge.get().createTunnel(_roboremote_port, _roboremote_port);
        }

        // see if a server is already listening
        boolean clientWasListening = false;
        if (Client.getInstance().isListening()) {
            clientWasListening = true;
        }

        if (clearAppData) {
            // clear app data - this has the side effect of killing a running app
            // TODO: this only works on 2.3+.. need a solution for 2.1+
            Device.clearAppData(app_package);
        }

        // wait for the client to stop listening if it was previously listening
        if (clientWasListening) {
            // wait for the server to be dead
            for (int x = 0; x < 10; x++) {
                // try to make a query.. if it doesnt work then sleep
                TestLogger.get().info("Trying to see if server is still available..");

                if (! Client.getInstance().isListening())
                    break;

                if (x == 9)
                    throw new Exception("Server is still available, but should not be");

                Thread.sleep(2000);
            }
        }

        if (! relaunch) {
            // start log listener
            TestLogger.get().info("Clearing logcat");
            DebugBridge.get().clearLogCat();

            TestLogger.get().info("Starting logcat");
            DebugBridge.get().startLogListener(System.getProperty("java.io.tmpdir") + 
            		File.separator + "adb_robo.log");

            // set up logger
            EmSingleton.intialize();

            EmSingleton.get().clearEvents();
        }

        // starting test runner
        TestLogger.get().info("Starting RC Runner");

        // start app
        startApp();
    }

    // This is called in the failure method override above
    public void tearDown() throws Exception {
        try
        {
            EmSingleton.get().close();
            killApp();

            // stop logcat
            TestLogger.get().info("Stopping logcat");
            DebugBridge.get().stopLogListener();

            // store logs
            Device.storeLogs("adb_robo.log", "robo.log");
        } catch (Exception e) {

        } finally {
            DebugBridge.get().close();
        }
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

    public static String getTestClass() {
        return test_class;
    }

    public static String getTestRunner() {
        return test_runner;
    }

    public static String getAppPackage() {
        return app_package;
    }

    public static void startApp() throws Exception {
        ap = new AppThread();
        ap.start();

        for (int x = 0; x < 10; x++) {
            // try to make a query.. if it doesnt work then sleep
            TestLogger.get().info("Trying to ping test server..");
            if (Client.getInstance().isListening())
                break;

            if (x == 9)
                throw new Exception("Could not contact test server");

            Thread.sleep(5000);
        }
    }

    public static void killApp() throws Exception {
        // try to kill just by calling exit
        try {
            Client.getInstance().map("java.lang.System", "exit", 0);
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

            if (! Client.getInstance().isListening())
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
        public void run() {
            try {
                DebugBridge.get().runShellCommand("am instrument -e port " + _roboremote_port + " -w " + getTestRunner(), new NullOutputReceiver(), 0);
            } catch (Exception e) {

            }
        }

        public void close() {
        }
    }
}
