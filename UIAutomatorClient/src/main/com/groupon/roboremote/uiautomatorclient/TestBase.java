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

package com.groupon.roboremote.uiautomatorclient;
import com.android.ddmlib.MultiLineReceiver;
import com.groupon.roboremote.roboremoteclientcommon.DebugBridge;
import com.groupon.roboremote.roboremoteclientcommon.Device;
import com.groupon.roboremote.roboremoteclientcommon.Utils;
import com.groupon.roboremote.roboremoteclientcommon.logging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.Exception;
import java.lang.String;
import java.lang.Thread;
import java.util.ArrayList;

public class TestBase {
    protected static final Logger logger = LoggerFactory.getLogger("test");
    private static String _automator_jar = null;
    private static String[] _automator_jars = null;
    private static ArrayList<String> _automator_run_jars = null;
    private static AppThread ap = null;
    private static boolean isStarted = false;
    private static LogcatLogger logcatLogger = null;

    public void onFailure() throws Exception {
        logger.warn("com.groupon.roboremote.uiautomatorclient.TestBase::OnFailure:: Taking screenshot");
        DebugBridge.get().getScreenShot("FAILURE.png");
        Device.storeFailurePng();
    }

    public void setUp(String testName) throws Exception {
        setUp(testName, true);
    }

    /**
     * This is the generic test setup function
     * @param clearAppData - true if you want app data cleared, false otherwise
     */
    public void setUp(String testName, Boolean clearAppData) throws Exception {
        if (_automator_jars == null) {
             setAppEnvironmentVariables();
            // push files to device only once at the beginning after setting the App environment variables
            deployTestJar();
        }

        // only do the following if isStarted==false OR the client is not already listening
        // this allows a client that overrides this class to safely call setUp multiple times without destroying logs
        if (!isStarted || !Client.getInstance().isListening()) {
            logger.info("Starting test {}", testName);
            Device.setupLogDirectories(testName);

            // see if a server is already listening
            boolean clientWasListening = false;
            if (Client.getInstance().isListening()) {
                clientWasListening = true;

                // try to kill it
                 killApp();
            }

            TestLogger.get().info("Starting logcat");
            if (logcatLogger == null) {
                logcatLogger = new LogcatLogger(System.getProperty("java.io.tmpdir") +
                        File.separator + "adb_uiauto.log");
            }
            logcatLogger.startLogListener();

            // set up event manager
            EmSingleton.get().clearEvents();

            // starting test runner
            TestLogger.get().info("Starting RC Runner");

            // start app
            startApp();

            isStarted = true;
        }
    }

    // This is called in the failure method override above
    public void tearDown() throws Exception {
        try
        {
            EmSingleton.release();
            killApp();

        } catch (Exception e) {

        } finally {
            // clear adb ports
            Utils.clearStaleADBTunnels("UIA");

            // stop logcat
            TestLogger.get().info("Stopping logcat");
            logcatLogger.stopLogListener();
            logcatLogger = null;

            // store logs
            Device.storeLogs("adb_uiauto.log", "uiauto.log");

            isStarted = false;
            DebugBridge.destroy();
        }
    }

    public static void setAppEnvironmentVariables(String ... automator_jars) {
        _automator_jars = automator_jars;
    }

    public static void setAppEnvironmentVariables() throws Exception {
        // get environment variables
        _automator_jar = Utils.getEnv("ROBO_UIAUTOMATOR_JAR", _automator_jar);
        if (_automator_jar == null) {
            throw new Exception("ROBO_UIAUTOMATOR_JAR is not set");
        }

        _automator_jars = _automator_jar.split(File.pathSeparator);
    }

    /**
     * Deploys the test jar to the device
     * @return
     * @throws Exception
     */
    public void deployTestJar() throws Exception {
        // we build a new list of jars that will be used for the launch command line
        _automator_run_jars = new ArrayList<String>();
        for (String jarFileName: _automator_jars) {
            File jarFile = new File(jarFileName);
            if (!jarFile.exists())
                throw new Exception("Test jar does not exist: " + _automator_jar);

            String[] destFileNameParts = jarFileName.split(File.separator);
            String destFileName = "/data/local/tmp/" + destFileNameParts[destFileNameParts.length - 1];
            _automator_run_jars.add(destFileName);

            logger.info("Push file to device :" + destFileName);
            DebugBridge.get().push(jarFileName, destFileName);

        }
    }

    public void startApp() throws Exception {
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

    public void killApp() throws Exception {
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
     * Gives external access to the logcat logger.  This is useful for runners that want to only call setup once
     * and need to restart the logger on their own
     * @return
     */
    public LogcatLogger getLogcatLogger() {
        return logcatLogger;
    }

    /**
     * Multi line receiver that prints to the console
     */
    private static class MultiReceiver extends MultiLineReceiver {
        boolean closed = false;

        public MultiReceiver() {
        }

        public void processNewLines(java.lang.String[] lines) {
            try {
                for (String line: lines) {
                    // TODO: add log levels
                    //System.out.println(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean isCancelled() {
            return closed;
        }

        public void close() {
            closed = true;
        }
    }

    /**
     * This thread contains the running RC test
     * DebugBridge does not return until the instrumentation finishes so we have to run it in its own thread
     */
    private static class AppThread extends Thread {
        MultiReceiver _receiver = null;

        public void run() {
            _receiver = new MultiReceiver();
            try {
                // clear stale ports
                Utils.clearStaleADBTunnels("UIA");

                // create adb tunnel
                PortSingleton.getInstance().setPort(Utils.getFreePort());
                Utils.addADBTunnelWithPIDFile("UIA", PortSingleton.getInstance().getPort());

                // build jar list
                String jarList = "";
                for (String jarFileName: _automator_run_jars) {
                    jarList += jarFileName + " ";
                }

                // run uiautomator
                String uiAutomatorCommand = "uiautomator runtest " + jarList + "-c com.groupon.roboremote.uiautomatorserver.RemoteTest -e port " + PortSingleton.getInstance().getPort();
                logger.info("Executing: {}", uiAutomatorCommand);
                DebugBridge.get().runShellCommand(uiAutomatorCommand, _receiver, 0);
            } catch (Exception e) {

            }
        }

        public void close() {
            // close the receiver to kill the thread
            _receiver.close();
        }
    }
}
