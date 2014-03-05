package com.groupon.roboremote.uiautomatorclient.components;

import com.groupon.roboremote.uiautomatorclient.Client;
import com.groupon.roboremote.uiautomatorclient.Constants;
import com.groupon.roboremote.uiautomatorclient.QueryBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: davidv
 * Date: 10/15/13
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Notification {


    public static void click(String notificationLabel) throws Exception {
        new QueryBuilder().map("com.android.uiautomator.core.UiSelector", "className", "android.widget.TextView").call("text", notificationLabel).execute();
        new QueryBuilder().map("com.android.uiautomator.core.UiObject", "clickAndWaitForNewWindow").execute();
    }
}
