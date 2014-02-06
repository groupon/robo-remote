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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.groupon.roboremote.roboremoteclientcommon.DebugBridge;
import com.groupon.roboremote.roboremoteclientcommon.Device;
import com.groupon.roboremote.uiautomatorclient.*;
import com.groupon.roboremote.uiautomatorclient.components.UiDevice;
import com.groupon.roboremote.uiautomatorclient.components.UiObject;
import com.groupon.roboremote.uiautomatorclient.components.UiSelector;
import junit.framework.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.json.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.lang.Exception;

public class AutomatorTests extends TestBase {
    @Rule
    public TestName name = new TestName();

    @Before
    public void setUpTest() throws Exception {
        // call super setup
        super.setUp(name.getMethodName());

        // also start helloworld
        DebugBridge.get().runShellCommand("pm clear com.groupon.roboremote.example.helloworld");
        DebugBridge.get().runShellCommand("am start -n com.groupon.roboremote.example.helloworld/.HelloWorld");
    }

    @After
    public void tearDownTest() throws Exception {
        tearDown();
    }

    @org.junit.Test
    public void BVT() throws Exception {
        UiObject fooLabel = new UiObject(new UiSelector().call("text", "Foo").call("className", "android.widget.TextView"));
        assertTrue(fooLabel.call("exists").getBoolean(0));
        fooLabel.call("click");
        UiObject fooPage = new UiObject(new UiSelector().call("text", "Foo page").call("className", "android.widget.TextView"));
        assertTrue(fooPage.call("exists").getBoolean(0));
    }

    @org.junit.Test
    public void NotificationTest() throws Exception {
        // click Notification
        UiObject fooLabel = new UiObject(new UiSelector().call("text", "Notification").call("className", "android.widget.TextView"));
        assertTrue(fooLabel.call("exists").getBoolean(0));
        fooLabel.call("click");

        // open the notification shade
        UiDevice.openNotification();

        // identify and click notification
        UiSelector selector = new UiSelector().call("className", "android.widget.TextView").call("text", "Test App Notification");
        UiObject notification = new UiObject(selector);
        assertTrue(notification.call("exists").getBoolean(0));
        notification.call("clickAndWaitForNewWindow");

        // verify page
        UiObject notificationPage = new UiObject(new UiSelector().call("text", "Notification page").call("className", "android.widget.TextView"));
        assertTrue(notificationPage.call("exists").getBoolean(0));
    }
}