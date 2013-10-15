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

import com.groupon.roboremote.roboremoteclientcommon.Device;
import com.groupon.roboremote.roboremoteclient.QueryBuilder;
import com.groupon.roboremote.roboremoteclient.Solo;
import com.groupon.roboremote.roboremoteclient.components.*;
import com.groupon.roboremote.roboremoteclient.junit.TestBase;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.json.*;

public class SampleTests extends TestBase {
    /**
     * This sets up the required environment variables for the tests to run
     * It overrides "setUpApp" from the roboremote testbase
     */
    @BeforeClass
    public static void setUpApp() {
        setAppEnvironmentVariables("com.groupon.roboremote.example.helloworld", "com.groupon.roboremote.example.helloworldtestrunner.Runner", "com.groupon.roboremote.example.helloworldtestrunner/android.test.InstrumentationTestRunner");
    }

    @Test
    public void BVT() {
        try {
            logger.info("Waiting for HelloWorld activity to start");
            assertTrue(Solo.waitForActivity("HelloWorld", 10000));
            
            logger.info("Checking to see if the string \"View\" exists on the screen");
            assertTrue(Text.exists("View"));
            
            // one way to click on the list
            Solo.clickInList(0);
            logger.info("Waiting for TestActivity activity to start");
            assertTrue(Solo.waitForActivity("TestActivity", 10000));
            
            logger.info("Going back");
            Solo.goBack();
            logger.info("Waiting for HelloWorld activity to start");
            assertTrue(Solo.waitForActivity("HelloWorld", 10000));
            
            // RoboRemote way to click on the list(can click any index.. even off screen)
            ListView.clickItemAtIndex(0);
            logger.info("Waiting for TestActivity activity to start");
            assertTrue(Solo.waitForActivity("TestActivity", 10000));
        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage());
            fail();
        }
    }

    @Test
    public void MethodChaining() {
        try {
            // create a querybuilder
            QueryBuilder query = new QueryBuilder();

            // get the size of the text from a textView
            // getTextSize returns a float.. if all is well this will be the 0th element in a JSON array
            // JSON only supports getDouble.. so we'll use that
            float textSize = new Double(query.map("solo", "getText", "View").call("getTextSize").execute().getDouble(0)).floatValue();
            logger.info("The size of the text item in the listview is: {}", textSize);

            // OK.. I see how that works.. but that line is reallllly long.. how else can I use that?
            // Now we will use the same mechanism for something more complicated.. like getting the real size of our listview
            query = new QueryBuilder();
            query.map("solo", "getCurrentListViews");
            query.call("get", 0);
            query.call("getAdapter");
            query.call("getCount");
            JSONArray results = query.execute();
            logger.info("Your listview is {} items long", results.getInt(0));
        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage());
            fail();
        }
    }

    @Test
    public void ViewPassing() {
        try {
            // I want to use clickOnView(android.view.View view) but your version takes a String instead of a View
            // Functions like getListViews() will return a String array of view pointers..
            // these strings can be passed directly into functions like clickOnView
            String[] views = Solo.getCurrentListViews();
            
            // ok now we'll try to get the text views from the first listView
            String[] textViews = Solo.getCurrentTextViews(views[0]);
            
            // now we'll try to click the first textview
            Solo.clickOnView(textViews[0]);
            
            Thread.sleep(4000);

        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage());
            fail();
        }
    }
    
    @Test
    public void WebViewContent() {
        try {
            logger.info("Waiting for HelloWorld activity to start");
            assertTrue(Solo.waitForActivity("HelloWorld", 10000));
            
            Solo.clickOnText("WebView");

            logger.info("Waiting for WebView activity to start");
            assertTrue(Solo.waitForActivity("WebviewActivity", 10000));
            
            
            QueryBuilder builder = new QueryBuilder();
            // request the HTML content via javascript
            builder.map("solo", "getView", "com.groupon.roboremote.example.helloworld.support.TestableWebView", 0).call("loadUrl", "javascript:window.HTMLOUT.showHTML(document.body.innerHTML);void(0);").execute();

            // wait for the content to be available to the test
            for (int x = 0; x < 10; x++) {
                builder = new QueryBuilder();
                Boolean available = builder.map("solo", "getView", "com.groupon.roboremote.example.helloworld.support.TestableWebView", 0).callField("htmlContentSet").execute().getBoolean(0);
                if (available)
                    break;
                
                if (x == 9) {
                    // there is a problem
                    throw new Exception("Could not get HTML content");
                }
                
                // just wait a few seconds before we try again
                Thread.sleep(5000);
            }
            
            // now try to get retstr from the view
            builder = new QueryBuilder();
            String htmlContent = builder.map("solo", "getView", "com.groupon.roboremote.example.helloworld.support.TestableWebView", 0).callField("htmlContent").execute().getString(0);

            assertTrue(htmlContent.equals("Testing text"));
        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage());
            fail();
        }
    }
}
