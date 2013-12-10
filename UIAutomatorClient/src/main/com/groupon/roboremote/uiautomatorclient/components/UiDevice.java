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

package com.groupon.roboremote.uiautomatorclient.components;

import com.groupon.roboremote.uiautomatorclient.Client;
import com.groupon.roboremote.uiautomatorclient.Constants;

public class UiDevice {
    /**
     * Open notification shade for API >= 18
     * @return
     * @throws Exception
     */
    public static boolean openNotification() throws Exception {
        boolean success = false;
        try {
            success = Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "openNotification").getBoolean(0);
        } catch (Exception e) {
            if (e.getMessage().contains("Could not find method")) {
                // try a different way
                int displayHeight = getDisplayHeight();

                // Calculated a Y position to pull down to that is the display height minus 10%
                int pullTo = displayHeight - (int)((double)displayHeight * .1);

                Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "swipe", 10, 0, 10, pullTo, 100);
                success = true;
            }
        }
        return success;
    }

    /**
     * Click at x,y position
     * @param x
     * @param y
     * @return
     * @throws Exception
     */
    public static boolean click(int x, int y) throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "click", x, y).getBoolean(0);
    }

    /**
     * Drag - API Level 18
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param steps
     * @return
     * @throws Exception
     */
    public static boolean drag(int startX, int startY, int endX, int endY, int steps) throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "drag", startX, startY, endX, endY, steps).getBoolean(0);
    }

    /**
     * Disables the sensors and freezes the device rotation at its current rotation state.
     * @throws Exception
     */
    public static void freezeRotation() throws Exception {
        Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "freezeRotation");
    }

    /**
     * Get the current package name
     * @return
     * @throws Exception
     */
    public static String getCurrentPackageName() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "getCurrentPackageName").getString(0);
    }

    public static int getDisplayHeight() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "getDisplayHeight").getInt(0);
    }

    public static int getDisplayWidth() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "getDisplayWidth").getInt(0);
    }

    public static int getDisplayRotation() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "getDisplayRotation").getInt(0);
    }

    public static String getLastTraversedText() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "getLastTraversedText").getString(0);
    }

    public static String getProductName() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "getProductName").getString(0);
    }

    public static boolean isNaturalOrientation() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "isNaturalOrientation").getBoolean(0);
    }

    public static boolean isScreenOn() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "isScreenOn").getBoolean(0);
    }

    /**
     * API 18 only
     * @return
     * @throws Exception
     */
    public static boolean openQuickSettings() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "openQuickSettings").getBoolean(0);
    }

    public static boolean pressBack() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressBack").getBoolean(0);
    }

    public static boolean pressDPadCenter() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressDPadCenter").getBoolean(0);
    }

    public static boolean pressDPadDown() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressDPadDown").getBoolean(0);
    }

    public static boolean pressDPadLeft() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressDPadLeft").getBoolean(0);
    }

    public static boolean pressDPadRight() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressDPadRight").getBoolean(0);
    }

    public static boolean pressDPadUp() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressDPadUp").getBoolean(0);
    }

    public static boolean pressDelete() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressDelete").getBoolean(0);
    }

    public static boolean pressEnter() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressEnter").getBoolean(0);
    }

    public static boolean pressHome() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressHome").getBoolean(0);
    }

    public static boolean pressKeyCode(int keyCode, int metaState) throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressKeyCode", keyCode, metaState).getBoolean(0);
    }

    public static boolean pressKeyCode(int keyCode) throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressKeyCode", keyCode).getBoolean(0);
    }

    public static boolean pressMenu() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressMenu").getBoolean(0);
    }

    public static boolean pressRecentApps() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressRecentApps").getBoolean(0);
    }

    public static boolean pressSearch() throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "pressSearch").getBoolean(0);
    }

    public static void setOrientationLeft() throws Exception {
        Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "setOrientationLeft");
    }

    public static void setOrientationNatural() throws Exception {
        Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "setOrientationNatural");
    }

    public static void setOrientationRight() throws Exception {
        Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "setOrientationRight");
    }

    public static void sleep() throws Exception {
        Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "sleep");
    }

    public static boolean swipe(int startX, int startY, int endX, int endY, int steps) throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "swipe", startX, startY, endX, endY, steps).getBoolean(0);
    }

   // TODO: implement this
   //  public boolean takeScreenshot (File storePath)

    public static void unfreezeRotation() throws Exception {
        Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "unfreezeRotation");
    }

    public static void waitForIdle(long timeout) throws Exception {
        Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "waitForIdle", timeout);
    }

    public static void waitForIdle() throws Exception {
        Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "waitForIdle");
    }

    public static boolean waitForWindowUpdate(String packageName, long timeout) throws Exception {
        return Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "waitForWindowUpdate", packageName, timeout).getBoolean(0);
    }

    public static void wakeUp() throws Exception {
        Client.getInstance().map(Constants.UIAUTOMATOR_UIDEVICE, "wakeUp");
    }
}
