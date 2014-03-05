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

package com.groupon.roboremote.roboremoteclient;

import java.lang.Exception;
import java.lang.String;
import java.util.ArrayList;

import com.groupon.roboremote.roboremoteclientcommon.Utils;
import org.json.JSONArray;

public class Solo {
    public static void assertCurrentActivity(String message, String name) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "assertCurrentActivity", message, name);
    }

    public static void assertCurrentActivity(String message, String name, boolean isNewInstance) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "assertCurrentActivity", message, name, isNewInstance);
    }

    public static void assertMemoryNotLow() throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "assertMemoryNotLow");
    }

    /**
     * Clears edit text for the specified widget
     * @param editText - Identifier for the correct widget(ex: android.widget.EditText@409af0b0) - Can be found using getViews
     */
    public static void clearEditText(String editText) throws Exception{
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clearEditText", editText);
    }

    public static void clearEditText(int index) throws Exception{
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clearEditText", index);
    }

    public static void clearLog() throws Exception{
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clearLog");
    }

    public static String[] clickInList(int line) throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickInList", line));
        return abar.toArray(new String[0]);
    }

    public static String[] clickInList(int line, int index) throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickInList", line, index));
        return abar.toArray(new String[0]);
    }

    public static String[] clickLongInList(int line) throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongInList", line));
        return abar.toArray(new String[0]);
    }

    public static String[] clickLongInList(int line, int index) throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongInList", line, index));
        return abar.toArray(new String[0]);
    }

    public static String[] clickLongInList(int line, int index, int time) throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongInList", line, index, time));
        return abar.toArray(new String[0]);
    }

    public static void clickLongOnScreen(float x, float y) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongOnScreen", x, y);
    }

    public static void clickLongOnScreen(float x, float y, int time) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongOnScreen", x, y, time);
    }

    public static void clickLongOnText(String text) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongOnText", text);
    }

    public static void clickLongOnText(String text, int match) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongOnText", text, match);
    }

    public static void clickLongOnText(String text, int match, boolean scroll) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongOnText", text, match, scroll);
    }

    public static void clickLongOnText(String text, int match, int time) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongOnText", text, match, time);
    }

    public static void clickLongOnTextAndPress(String text, int index) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongOnTextAndPress", text, index);
    }

    public static void clickLongOnView(String view) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongOnView", view);
    }

    public static void clickLongOnView(String view, int time) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickLongOnView", view, time);
    }
    
    public static void clickOnActionBarHomeButton() throws Exception {
    	Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnActionBarHomeButton");
    }

    public static void clickOnActionBarItem(int resourceId) throws Exception{
    	Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnActionBarItem", resourceId);
    }

    public static void clickOnButton(int index) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnButton", index);
    }

    public static void clickOnButton(String name) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnButton", name);
    }

    public static void clickOnCheckBox(int index) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnCheckBox", index);
    }

    public static void clickOnEditText(int index) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnEditText", index);
    }

    public static void clickOnImage(int index) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnImage", index);
    }

    public static void clickOnImageButton(int index) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnImageButton", index);
    }

    public static void clickOnMenuItem(String text) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnMenuItem", text);
    }

    public static void clickOnMenuItem(String text, boolean subMenu) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnMenuItem", text, subMenu);
    }

    public static void clickOnRadioButton(int index) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnRadioButton", index);
    }

    public static void clickOnScreen(float x, float y) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnScreen", x, y);
    }

    public static void clickOnScreen(float x, float y, int numberOfClicks) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnScreen", x, y, numberOfClicks);
    }

    public static void clickOnText(String text) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnText", text);
    }

    public static void clickOnText(String text, int match) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnText", text, match);
    }

    public static void clickOnText(String text, int match, boolean scroll) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnText", text, match, scroll);
    }

    public static void clickOnToggleButton(String name) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnToggleButton", name);
    }

    public static void clickOnView(String viewName) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnView", viewName);
    }

    public static void clickOnView(String viewName, boolean immediately) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "clickOnView", viewName, immediately);
    }

    public static void drag(float fromX, float toX, float fromY, float toY, int stepCount)  throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "drag", fromX, toX, fromY, toY, stepCount);
    }

    public static void enterText(String editText, String text) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "enterText", editText, text);
    }

    public static void enterText(int index, String text) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "enterText", index, text);
    }
    
    public static void finishInactiveActivities() throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "finishInactiveActivities");
    }

    public static String[] getAllOpenedActivities() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getAllOpenedActivities"));
        return abar.toArray(new String[0]);
    }

    public static String getButton(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getButton", index).getString(0);
    }

    public static String getButton(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getButton", text).getString(0);
    }

    public static String getButton(String text, boolean onlyVisible) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getButton", text, onlyVisible).getString(0);
    }

    public static String getCurrentActivity() throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentActivity").getString(0);
    }

    public static String[] getCurrentButtons() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.Button"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentCheckBoxes() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.CheckBox"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentDatePickers() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.DatePicker"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentEditTexts() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.EditText"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentGridViews() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.EditText"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentImageButtons() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widgetImageButton"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentImageViews() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.ImageView"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentImageViews(String parent) throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widgetImageView", parent));
        return abar.toArray(new String[0]);
    }


    public static String[] getCurrentListViews() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.ListView"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentProgressBars() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.ProgressBar"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentRadioButtons() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.RadioButton"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentScrollViews() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.ScrollView"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentSlidingDrawers() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.SlidingDrawer"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentSpinners() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.Spinner"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentTextViews(String parent) throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.TextView", parent));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentTimePickers() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.TimePicker"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentToggleButtons() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", "android.widget.ToggleButton"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentViews() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews"));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentViews(String classToFilterBy) throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", classToFilterBy));
        return abar.toArray(new String[0]);
    }

    public static String[] getCurrentViews(String classToFilterBy, String parent) throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getCurrentViews", classToFilterBy, parent));
        return abar.toArray(new String[0]);
    }

    public static String getEditText(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getEditText", index).getString(0);
    }

    public static String getEditText(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getEditText", text).getString(0);
    }

    public static String getEditText(String text, boolean onlyVisible) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getEditText", text, onlyVisible).getString(0);
    }

    public static String getImage(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getImage", index).getString(0);
    }

    public static String getImageButton(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getImageButton", index).getString(0);
    }

    public static String getString(int resId) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getString", resId).getString(0);
    }

    public static String getText(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getText", index).getString(0);
    }

    public static String getText(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getText", text).getString(0);
    }

    public static String getText(String text, boolean onlyVisible) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getText", text, onlyVisible).getString(0);
    }

    public static String getTopParent(String view) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getTopParent", view).getString(0);
    }

    public static String getView(int id) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getView", id).getString(0);
    }

    public static String getView(int id, int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getView", id, index).getString(0);
    }

    public static String getView(String viewClass, int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getView", viewClass, index).getString(0);
    }

    public static String[] getViews() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getViews"));
        return abar.toArray(new String[0]);
    }

    public static String[] getViews(String parent) throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getViews", parent));
        return abar.toArray(new String[0]);
    }

    public static String getWebUrl() throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getWebUrl").getString(0);
    }

    public static void goBack() throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "goBack");
    }

    public static void goBackToActivity(String name) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "goBackToActivity", name);
    }

    public static void hideSoftKeyboard() throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "hideSoftKeyboard");
    }

    public static boolean isCheckBoxChecked(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "isCheckBoxChecked", index).getBoolean(0);
    }

    public static boolean isCheckBoxChecked(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "isCheckBoxChecked", text).getBoolean(0);
    }

    public static boolean isRadioButtonChecked(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "isRadioButtonChecked", index).getBoolean(0);
    }

    public static boolean isRadioButtonChecked(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "isRadioButtonChecked", text).getBoolean(0);
    }

    public static boolean isSpinnerTextSelected(int index, String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "isSpinnerTextSelected", index, text).getBoolean(0);
    }

    public static boolean isSpinnerTextSelected(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "isSpinnerTextSelected", text).getBoolean(0);
    }

    public static boolean isTextChecked(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "isTextChecked", text).getBoolean(0);
    }

    public static boolean isToggleButtonChecked(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "isToggleButtonChecked", index).getBoolean(0);
    }

    public static boolean isToggleButtonChecked(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "isToggleButtonChecked", text).getBoolean(0);
    }

    public static void pressMenuItem(int index) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "pressMenuItem", index);
    }

    public static void pressMenuItem(int index, int itemsPerRow) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "pressMenuItem", index, itemsPerRow);
    }

    public static void pressSpinnerItem(int spinnerIndex, int itemIndex)  throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "pressSpinnerItem", spinnerIndex, itemIndex);
    }

    public static boolean scrollDown() throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollDown").getBoolean(0);
    }

    public static boolean scrollDownList(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollDownList", index).getBoolean(0);
    }

    public static boolean scrollDownList(String list) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollDownList", list).getBoolean(0);
    }

    public static boolean scrollListToBottom(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollListToBottom", index).getBoolean(0);
    }

    public static boolean scrollListToBottom(String view) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollListToTop", view).getBoolean(0);
    }

    public static boolean scrollListToTop(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollListToTop", index).getBoolean(0);
    }

    public static boolean scrollListToTop(String view) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollListToTop", view).getBoolean(0);
    }

    public static void scrollToSide(int side) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollToSide", side);
    }

    public static void scrollToBottom() throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollToBottom");
    }
    
    public static void scrollToTop() throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollToTop");
    }

    public static boolean scrollUp() throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollUp").getBoolean(0);
    }

    public static boolean scrollUpList(int index) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollUpList", index).getBoolean(0);
    }

    public static boolean scrollUpList(String view) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollUpList", view).getBoolean(0);
    }

    public static boolean searchButton(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchButton", text).getBoolean(0);
    }

    public static boolean searchButton(String text, boolean onlyVisible) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchButton", text, onlyVisible).getBoolean(0);
    }

    public static boolean searchButton(String text, int minimumNumberOfMatches) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchButton", text, minimumNumberOfMatches).getBoolean(0);
    }

    public static boolean searchButton(String text, int minimumNumberOfMatches, boolean onlyVisible) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchButton", text, minimumNumberOfMatches, onlyVisible).getBoolean(0);
    }

    public static boolean searchEditText(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchEditText", text).getBoolean(0);
    }

    public static boolean searchText(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchText", text).getBoolean(0);
    }

    public static boolean searchText(String text, boolean onlyVisible) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchText", text, onlyVisible).getBoolean(0);
    }

    public static boolean searchText(String text, int minimumNumberOfMatches) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchText", text, minimumNumberOfMatches).getBoolean(0);
    }

    public static boolean searchText(String text, int minimumNumberOfMatches, boolean scroll) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchText", text, minimumNumberOfMatches, scroll).getBoolean(0);
    }

    public static boolean searchText(String text, int minimumNumberOfMatches, boolean scroll, boolean onlyVisible) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchText", text, minimumNumberOfMatches, scroll, onlyVisible).getBoolean(0);
    }

    public static boolean searchToggleButton(String text) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchToggleButton", text).getBoolean(0);
    }

    public static boolean searchToggleButton(String text, int minimumNumberOfMatches) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "searchToggleButton", text, minimumNumberOfMatches).getBoolean(0);
    }

    public static void sendKey(int key) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "sendKey", key);
    }

    public static void setActivityOrientation(int orientation) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "setActivityOrientation", orientation);
    }

    public static void setDatePicker(String datePicker, int year, int monthOfYear, int dayOfMonth) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "setDatePicker", datePicker, year, monthOfYear, dayOfMonth);
    }

    public static void setDatePicker(int index, int year, int monthOfYear, int dayOfMonth) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "setDatePicker", index, year, monthOfYear, dayOfMonth);
    }

    public static void setProgressBar(int index, int progress) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "setProgressBar", index,  progress);
    }

    public static void setProgressBar(String progressBar, int progress) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "setProgressBar", progressBar,  progress);
    }

    public static void setSlidingDrawer(int index, int status) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "setSlidingDrawer", index,  status);
    }

    public static void setSlidingDrawer(String slidingDrawer, int status) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "setSlidingDrawer", slidingDrawer,  status);
    }

    public static void setTimePicker(int index, int hour, int minute)  throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "setTimePicker", index,  hour, minute);
    }

    public static void setTimePicker(String timePicker, int hour, int minute)  throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "setTimePicker", timePicker,  hour, minute);
    }

    public static void sleep(int time) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "sleep", time);
    }

    public void scrollListToLine(int index, int line)  throws Exception{
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollListToLine", index, line);
    }

    public void scrollListToLine(String view, int line)  throws Exception{
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollListToLine", view, line);
    }

    public static void scrollViewToSide(String view, int side) throws Exception  {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "scrollViewToSide", view, side);
    }

    public static void startScreenshotSequence(String name) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "startScreenshotSequence", name);
    }

    public static void startScreenshotSequence(String name, int quality, int frameDelay, int maxFrames) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "startScreenshotSequence", name, quality, frameDelay, maxFrames);
    }

    public static void stopScreenshotSequence(String name) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "stopScreenshotSequence");
    }

    public static void takeScreenshot() throws Exception {
    	Client.getInstance().map(Constants.ROBOTIUM_SOLO, "takeScreenshot");
    }

    public static void takeScreenshot(String name) throws Exception {
    	Client.getInstance().map(Constants.ROBOTIUM_SOLO, "takeScreenshot", name);
    }
    
    public static void typeText(int index, String text) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "typeText", index, text);
    }

    public static void typeText(String editText, String text) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "typeText", editText, text);
    }

    public static boolean waitForActivity(String name) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForActivity", name).getBoolean(0);
    }

    public static boolean waitForActivity(String name, int timeout) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForActivity", name, timeout).getBoolean(0);
    }

    public static boolean waitForCondition(String condition, int timeout) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForActivity", condition, timeout).getBoolean(0);
    }

    public static boolean waitForDialogToClose(long timeout) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForDialogToClose", timeout).getBoolean(0);
    }

	public static boolean waitForFragmentById(int id) throws Exception {
		return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForFragmentById", id).getBoolean(0);
	}

	public static boolean waitForFragmentByTag(String tag) throws Exception {
		return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForFragmentByTag", tag).getBoolean(0);
	}
	
	public static boolean waitForLogMessage(String logMessage) throws Exception {
		return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForLogMessage", logMessage).getBoolean(0);
	}
	

    public static boolean waitForText(String text, int minimumNumberOfMatches, long timeout) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForText", text, minimumNumberOfMatches, timeout).getBoolean(0);
    }

    public static boolean waitForText(String text, int minimumNumberOfMatches, long timeout, boolean scroll) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForText", text, minimumNumberOfMatches, timeout, scroll).getBoolean(0);
    }

    public static boolean waitForText(String text, int minimumNumberOfMatches, long timeout, boolean scroll, boolean onlyVisible) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForText", text, minimumNumberOfMatches, timeout, scroll, onlyVisible).getBoolean(0);
    }

    public static String waitForView(String viewClass, int minimumNumberOfMatches, int timeout) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForView", viewClass, minimumNumberOfMatches, timeout).getString(0);
    }

    public static String waitForView(String viewClass, int minimumNumberOfMatches, int timeout, boolean scroll) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForView", viewClass, minimumNumberOfMatches, timeout, scroll).getString(0);
    }

    public static String waitForView(String view) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForView", view).getString(0);
    }

    public static String waitForView(String view, int timeout, boolean scroll) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForView", view, timeout, scroll).getString(0);
    }

    public static String waitForView(int id ) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForView", id).getString(0);
    }

    public static String waitForView(int id, int minimumNumberOfMatches, int timeout) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForView", id, minimumNumberOfMatches, timeout).getString(0);
    }

    public static String waitForView(int id, int minimumNumberOfMatches, int timeout, boolean scroll) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForView", id, minimumNumberOfMatches, timeout, scroll).getString(0);
    }


    /**
     * Returns a string array of all of the text contained within a view
     * @param viewName
     * @return
     */
    public static String[] getTextFromView(String viewName) throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getTextFromView", viewName));
        return abar.toArray(new String[0]);
    }

    public static void waitForHintText(String hintText, int timeout) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForHintText", hintText, timeout);
    }

    public static String[] getVisibleText() throws Exception {
        ArrayList<String> abar = Utils.jsonArrayToStringList(Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getVisibleText"));
        return abar.toArray(new String[0]);
    }
    
    public static int getResourceId(String namespace, String resourceType, String resourceName) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getResourceId", namespace, resourceType, resourceName).getInt(0);
    }

    /**
     * SOLO2 addons
     */
    public static boolean isVisible(String view) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "isVisible", view).getBoolean(0);
    }

    public static boolean waitForActivity(String applicationName, String activityName, int timeout) throws Exception {
        return Client.getInstance().map(Constants.ROBOTIUM_SOLO, "waitForActivity", applicationName, activityName, timeout).getBoolean(0);
    }

    public static void waitForViewToBeVisible(String view, int timeout) throws Exception {
        if (timeout < 250) {
            throw new Exception("Timeout must be greater than 250");
        }

        final int retryPeriod = 250;
        int retryNum = timeout / retryPeriod;
        for (int i = 0; i < retryNum; i++) {
            if (isVisible(view)) {
                break;
            }
            if (i == retryNum - 1) {
                throw new Exception("View did not become visible");
            }
            Thread.sleep(retryPeriod);
        }
    }

    public static void waitForViewToBeVisible(int viewID, int timeout) throws Exception {
        if (timeout < 250) {
            throw new Exception("Timeout must be greater than 250");
        }

        final int retryPeriod = 250;
        int retryNum = timeout / retryPeriod;
        for (int i = 0; i < retryNum; i++) {
            // first see if the view even exists yet
            try {
                String view = Solo.getView(viewID);
                if (isVisible(view)) {
                    break;
                }
            } catch (Exception e) {
                // do nothing.. this is fine
            }

            if (i == retryNum - 1) {
                throw new Exception("View did not become visible");
            }
            Thread.sleep(retryPeriod);
        }
    }

    public static void enterTextAndWait(int fieldResource, String value) throws Exception {
        Client.getInstance().map(Constants.ROBOTIUM_SOLO, "enterTextAndWait", fieldResource, value);
    }

    /**
     * Get the location of a view on the screen
     * @param view - string reference to the view
     * @return - int array(x, y) of the view location
     * @throws Exception
     */
    public static int[] getLocationOnScreen(String view) throws Exception {
        int[] location = new int[2];
        
        JSONArray results = Client.getInstance().map(Constants.ROBOTIUM_SOLO, "getLocationOnScreen", view);
        location[0] = results.getInt(0);
        location[1] = results.getInt(1);

        return location;
    }
}
