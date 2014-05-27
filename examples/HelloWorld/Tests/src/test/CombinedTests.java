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

import com.groupon.roboremote.roboremoteclient.Solo;
import com.groupon.roboremote.roboremoteclient.junit.TestBase;
import com.groupon.roboremote.uiautomatorclient.components.UiObject;
import com.groupon.roboremote.uiautomatorclient.components.UiScrollable;
import com.groupon.roboremote.uiautomatorclient.components.UiSelector;
import org.junit.*;

import static junit.framework.Assert.assertTrue;

public class CombinedTests extends TestBase {
    @BeforeClass
    public static void setUpApp() {
        setAppEnvironmentVariables("com.groupon.roboremote.example.helloworld", "com.groupon.roboremote.example.helloworldtestrunner.Runner", "com.groupon.roboremote.example.helloworldtestrunner/com.groupon.roboremote.roboremoteserver.RemoteTestRunner");
    }

    @Test
    public void BVT() throws Exception {
        // First use Robotium to get the current string representation of the "view" resource
        String view = Solo.getString(Solo.getResourceId("com.groupon.roboremote.example.helloworld.R", "string", "view"));

        // Now setup the UiScrollable
        UiScrollable listView = new UiScrollable(new UiSelector());
        listView.call("setMaxSearchSwipes", 100);
        listView.call("scrollTextIntoView", view);
        listView.call("waitForExists", 5000);

        // create a TestView selector
        UiSelector textViewSelector = new UiSelector().call("className", "android.widget.TextView");

        // Call getChildByText on the TextView selector
        listView.call("getChildByText", textViewSelector.getStoredValue(), view);

        // Create a UiObject from the result of the getChildByText call
        UiObject viewItemObject = new UiObject(listView.getLastResult());
        viewItemObject.call("click");

        // Create a UiObject to assert that "View page" exists on the screen
        UiObject viewPage = new UiObject(new UiSelector().call("text", view + " page").call("className", "android.widget.TextView"));
        assertTrue(viewPage.call("exists").getBoolean(0));
    }


}
