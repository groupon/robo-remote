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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.groupon.roboremote.roboremoteclient.logging.*;

import java.io.File;
import java.lang.Exception;
import java.lang.Object;
import java.lang.String;
import java.lang.Thread;

public class TestBase {
    public static final Logger logger = LoggerFactory.getLogger("test");
    private Object syncObject_ = new Object();

    public static void onFailure() throws Exception {
        logger.warn("TestBase::OnFailure:: Taking screenshot");
        DebugBridge.get().getScreenShot("FAILURE.png");
        Device.storeFailurePng();
    }
    
    public static void setUp(String testName) throws Exception {
        setUp(testName, false, true);
    }

    /**
     * This is the generic test setup function
     * @param relaunch - true if this is an app relaunch
     * @param clearAppData - true if you want app data cleared, false otherwise
     */
    public static void setUp(String testName, Boolean relaunch, Boolean clearAppData) throws Exception{
        if (! relaunch) {
            logger.info("Starting test {}", testName);
            Utils.setTestName(testName);
            Device.setupLogDirectories();

            // create adb tunnel
            DebugBridge.get().createTunnel(8080, 8080);
        }

        // see if a server is already listening
        boolean clientWasListening = false;
        if (Client.isListening()) {
            clientWasListening = true;
        }

        if (clearAppData) {
            // clear app data - this has the side effect of killing a running app
            // TODO: this only works on 2.3+.. need a solution for 2.1+
            Device.clearAppData();
        }

        // wait for the client to stop listening if it was previously listening
        if (clientWasListening) {
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
        Device.startApp();
    }

    // This is called in the failure method override above
    public void tearDown() throws Exception {
        try
        {
            EmSingleton.get().close();
            Device.killApp();

            // stop logcat
            TestLogger.get().info("Stopping logcat");
            DebugBridge.get().stopLogListener();

            // store logs
            Device.storeLogs();
        } catch (Exception e) {

        } finally {
            DebugBridge.get().close();
        }
    }
}
