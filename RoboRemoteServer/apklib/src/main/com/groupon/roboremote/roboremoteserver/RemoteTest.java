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

package com.groupon.roboremote.roboremoteserver;

import org.junit.Before;
import org.junit.Rule;
import android.app.Activity;
import android.app.Instrumentation;
import com.groupon.roboremote.Constants;
import com.groupon.roboremote.roboremoteserver.robotium.*;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

public abstract class RemoteTest<T extends Activity> {
    protected Solo2 solo;
    private Object lastResponseObject = null;
    private Boolean appStarted = false;
    private RoboRemoteServer rrs = null;

    @Rule
    public ActivityTestRule<T> mActivityRule;

    public RemoteTest(Class<T> activityClass) throws ClassNotFoundException {
        rrs = new RoboRemoteServer(null, getInstrumentation(), this);
        mActivityRule = new ActivityTestRule(activityClass);
    }

    public Instrumentation getInstrumentation() {
        return InstrumentationRegistry.getInstrumentation();
    }

    public void startServer() throws Exception {
        int port = Constants.ROBOREMOTE_SERVER_PORT;

        if (System.getProperty("ROBOREMOTE_PORT") != null) {
            port = Integer.parseInt(System.getProperty("ROBOREMOTE_PORT"));
        }

        System.out.println("RoboRemote: Starting on port: " + port);
        rrs.startServer(port);
    }

    public void startApp() {
        // Initialize Robotium Solo singleton
        solo = new Solo2(getInstrumentation(), mActivityRule.getActivity());
        SoloSingleton.set(solo);
        rrs.setSolo(solo);
    }

    @Before
    public void setup() throws Exception {
        startApp();
    }
}
