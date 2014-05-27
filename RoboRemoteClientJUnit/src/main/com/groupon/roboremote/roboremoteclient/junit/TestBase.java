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

package com.groupon.roboremote.roboremoteclient.junit;

import com.groupon.roboremote.Constants;
import com.groupon.roboremote.roboremoteclientcommon.Utils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestName;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import static org.junit.Assert.fail;

public class TestBase extends com.groupon.roboremote.roboremoteclient.TestBase {
    @Rule
    public TestName name = new TestName();
    protected void onFail(FrameworkMethod m){}
    protected void onPass(FrameworkMethod m){}

    // This rule is evaluated at the end of a test run
    // If there as an AssertionError(test failure) then this takes a screenshot
    // This also calls test teardown
    @Rule
    public MethodRule mr = new MethodRule() {
        public Statement apply(final Statement base, final FrameworkMethod m, Object o) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    AssertionError error = null;

                    boolean failed = false;

                    try {
                        base.evaluate();
                    } catch (AssertionError e) {
                        // call global onFailure
                        onFailure();

                        failed = true;

                        error = e;
                    }

                    // tear down robotium remote
                    tearDown();

                    // tear down uiautomator remote
                    if (Utils.getEnv("ROBO_UIAUTOMATOR_JAR", null) != null) {
                        com.groupon.roboremote.uiautomatorclient.TestBase.killApp();
                    }

                    if (failed)
                    {
                        // do failure stuff here
                        // for writing own fail function in custom com.groupon.roboremote.uiautomatorclient.TestBase
                        onFail(m);

                    } else {
                        // do passed stuff here
                        // for writing own pass function in custom com.groupon.roboremote.uiautomatorclient.TestBase
                        onPass(m);

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
        setAppEnvironmentVariables();
    }

    @Before
    public void setUp() throws Exception {
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
            // try to setup UIAutomator if it is available
            // base availability on ROBO_UIAUTOMATOR_JAR being set
            if (Utils.getEnv("ROBO_UIAUTOMATOR_JAR", null) != null) {
                logger.info("Starting UI Automator...");
                com.groupon.roboremote.uiautomatorclient.TestBase.setUp(getTestName());
            }

            // setup robotium remote
            setUp(getTestName(), relaunch, clearAppData);
        } catch (Exception e) {
            fail("Caught exception: " + e);
        }
    }

    /**
     * This should be called if the test name is needed somewhere in a junit test
     */
    public String getTestName() {
        String subname = name.getMethodName();

        String test_name = this.getClass() + "_" + subname;
        test_name = test_name.replaceFirst("class ", "");

        return test_name;
    }
}
