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

package com.roboremote.roboremoteclient;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestName;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.roboremote.roboremoteclient.logging.*;

import java.lang.AssertionError;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.lang.Runtime;
import java.lang.String;
import java.lang.Thread;
import java.lang.Throwable;
import java.util.ArrayList;

import static org.junit.Assert.fail;

public class TestBase {
    public static final Logger logger = LoggerFactory.getLogger("test");
    private Object syncObject_ = new Object();

    @Rule
    public TestName name = new TestName();

    // This rule is evaluated at the end of a test run
    // If there as an AssertionError(test failure) then this takes a screenshot
    // This also calls test teardown
    @Rule
    public MethodRule mr = new MethodRule() {
        public Statement apply(final Statement base, FrameworkMethod m, Object o) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    AssertionError error = null;

                    boolean failed = false;

                    try {
                        base.evaluate();
                    } catch (AssertionError e) {
                        logger.warn("TestBase::OnFailure:: Taking screenshot");
                        DebugBridge.get().getScreenShot("FAILURE.png");
                        Device.storeFailurePng();

                        failed = true;

                        error = e;
                    }

                    tearDown();

                    if (failed)
                    {
                        // do failure stuff here
                        
                    } else {
                        // do passed stuff here

                    }

                    // If there is an error to re-throw.. throw it..
                    if (error != null)
                        throw error;
                }
            };
        }
    };

    @BeforeClass
    public static void setUpApp() throws Exception {
        Device.setAppEnvironmentVariables();
    }
    
    @Before
    public void setUp() {
        setUp(false, true);
    }

    /**
     * This is the generic test setup function
     * @param relaunch - true if this is an app relaunch
     * @param clearAppData - true if you want app data cleared, false otherwise
     */
    public void setUp(Boolean relaunch, Boolean clearAppData) {
        try
        {
            String subname = name.getMethodName();

            String test_name = this.getClass() + "_" + subname;
            test_name = test_name.replaceFirst("class ", "");
            if (! relaunch) {
                logger.info("Starting test {}", test_name);
                Utils.setTestName(test_name);
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
                DebugBridge.get().startLogListener("/tmp/adb_robo.log");

                // set up logger
                EmSingleton.intialize();

                EmSingleton.get().clearEvents();
            }

            // starting test runner
            TestLogger.get().info("Starting RC Runner");

            // start app
            Device.startApp();

        } catch (Exception e) {
            fail("Caught exception: " + e);
        }
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
            fail("Caught exception: " + e);
        } finally {
            DebugBridge.get().close();
        }
    }
}
