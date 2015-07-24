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

package com.groupon.roboremote.roboremoteserver.robotium;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.robotium.solo.Solo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Solo2 extends Solo{

    public Solo2(Instrumentation instrumentation)
    {
        super(instrumentation);
    }

    public Solo2(Instrumentation instrumentation, Activity activity)
    {
        super(instrumentation, activity);
    }

    /**
     * Filters through all views in current activity and gets those that inherit from a given class to filter by
     * @param classToFilterBy - The class type by which to filter by.
     * @param <T> Type representing a class
     * @return - an ArrayList of views matching the filter
     */
    public <T extends View> ArrayList<T> getFilteredViews(Class<T> classToFilterBy) {
		ArrayList<T> filteredViews = new ArrayList<T>();
		List<View> allViews = this.getViews();
		for(View view : allViews){
			if (view != null && classToFilterBy.isAssignableFrom(view.getClass())) {
				filteredViews.add(classToFilterBy.cast(view));
			}
		}
		return filteredViews;
	}

    /**
     * Searches through all views and gets those containing text which are visible. Stips the text form each view and
     * returns it as an arraylist.
     * @return - ArrayList contating text in the current view
     */
    public ArrayList<String> getVisibleText()
    {
        ArrayList<TextView> textViews = getFilteredViews(TextView.class);

        ArrayList<String> allStrings = new ArrayList<String>();
        for(TextView v: textViews)
        {
            if(v.getVisibility() == View.VISIBLE)
            {
                String s = v.getText().toString();
                allStrings.add(s);
            }
        }

        return allStrings;
    }

    /**
     * Override the normal robotium getView to use one that retries over a 10s period
     * @param id of view to return
     * @return
     */
    public View getView(int id)
    {
        return getView(id, 10000);
    }

    /**
     * Extend the normal robotium getView to retry every 250ms over 10s to get the view requested
     * @param id Resource id of the view
     * @param timeout amount of time to retry getting the view
     * @return View that we want to find by id
     */
    public View getView(int id, int timeout) {
        View v = null;
        int RETRY_PERIOD = 250;
        int retryNum = timeout / RETRY_PERIOD;

        for (int i = 0; i < retryNum; i++) {
            try {
                v = super.getView(id);
            } catch (Exception e) {}

            if (v != null) {
                break;
            }
            this.sleep(RETRY_PERIOD);
        }
        return v;
    }

    /**
     *  Post a .performClick action to the view directly. More info on why we do this the way we do (took me
     *  forever to figure this out):
     *  http://stackoverflow.com/questions/2087164/calledfromwrongthreadexception-exercising-junit-tests-on-android
     *  http://stackoverflow.com/questions/5878844/need-help-to-make-android-button-visible
     * @param V the view to click on
     */
    public void clickOnViewDirect(View V)
    {
        final View viewToClick = V;
        viewToClick.post(new Runnable() {
            @Override
            public void run() {
                viewToClick.performClick();
            }
        }
        );
    }

    /**
     * Wait for a resource to become active
     *
     * @param res     - view resource
     * @param timeout - timeout period to keep retrying
     * @param <T>     - View type
     */
    public <T> boolean waitForResource(int res, int timeout) {
        int RETRY_PERIOD = 250;
        int retryNum = timeout / RETRY_PERIOD;
        for (int i = 0; i < retryNum; i++) {
            T View = (T) this.getView(res);
            if (View != null) {
                break;
            }
            if (i == retryNum - 1) {
                return false;
            }
            this.sleep(RETRY_PERIOD);
        }
        return true;
    }

     /**
     * Wait for activity to become active (by string)
     * @param applicationName
     * @param activityName
     * @param retryTime
     */
    public boolean waitForActivity(String applicationName, String activityName, int retryTime) {
        ComponentName componentName = new ComponentName(applicationName, activityName);
        return waitForActivity(componentName, retryTime);
    }

    /**
     * Wait for activity to become active (by component name)
     *
     * @param name
     * @param retryTime
     */
    public boolean waitForActivity(ComponentName name, int retryTime) {
        final int retryPeriod = 250;
        int retryNum = retryTime / retryPeriod;
        for (int i = 0; i < retryNum; i++) {
            if (this.getCurrentActivity().getComponentName().equals(name)) {
                break;
            }
            if (i == retryNum - 1) {
                return false;
            }
            this.sleep(retryPeriod);
        }
        return true;
    }

    public void clickOnTextAlt(String text) throws Exception
    {
        boolean stillScrolling = true;
        while (stillScrolling)
        {
            TextView v = this.getText(text);

            if(v != null)
            {
                this.clickOnView(v);
                return;
            }

            stillScrolling = this.scrollDown();
        }

        throw new Exception(String.format("TextView not found: %s", text));

    }
    
    /**
     * Gets views with a custom class name
     * @param viewName simple class name of the view type
     * @return arraylist of views matching class name provided
     */
    public ArrayList<View> getCustomViews(String viewName)
    {
        ArrayList<View> returnViews = new ArrayList<View>();
        for(View v: this.getViews())
        {
            if(v.getClass().getSimpleName().equals(viewName))
                returnViews.add(v);
        }

        return returnViews;
    }

    /**
     * Waits for hint text to appear in an EditText
     * @param hintText text to wait for
     * @param timeout amount of time to wait
     */
    public void waitForHintText(String hintText, int timeout) throws Exception
    {
        int RETRY_PERIOD = 250;
        int retryNum = timeout / RETRY_PERIOD;
        for(int i=0; i < retryNum; i++)
        {
            ArrayList<View> imageViews = getCustomViews("EditText");
            for(View v: imageViews)
            {
                if(((EditText)v).getHint() == null)
                    continue;
                if(((EditText)v).getHint().equals(hintText) &&
                        v.getVisibility() == View.VISIBLE)
                    return;
            }

            this.sleep(RETRY_PERIOD);
        }
        throw new Exception(String.format("Splash screen didn't disappear after %d ms", timeout));
    }

      /**
     * Enter text into a given field resource id
     * @param fieldResource - Resource id of a field (R.id.*)
     * @param value - value to enter into the given field
     */
    public void enterTextAndWait(int fieldResource, String value)
    {
        EditText textBox = (EditText) this.getView(fieldResource);
        this.enterText(textBox, value);
        this.waitForText(value);
    }

    /**
     * Returns a string for a resourceId in the specified namespace
     * @param namespace
     * @param resourceId
     * @return
     * @throws Exception
     */
    public String getLocalizedResource(String namespace, String resourceId) throws Exception {
        String resourceValue = "";

        Class r = Class.forName(namespace + "$string");
        Field f = r.getField(resourceId);
        resourceValue = getCurrentActivity().getResources().getString(f.getInt(f));

        return resourceValue;
    }

    /**
     * Returns a string array for a specified string-array resourceId in the specified namespace
     * @param namespace
     * @param resourceId
     * @return
     * @throws Exception
     */
    public String[] getLocalizedResourceArray(String namespace, String resourceId) throws Exception {
        Class r = Class.forName(namespace + "$string");
        Field f = r.getField(resourceId);
        return getCurrentActivity().getResources().getStringArray(f.getInt(f));
    }
    
    public int getResourceId(String namespace, String resourceType, String resourceName) throws Exception {
        Class r = Class.forName(namespace + "$" + resourceType);
        return r.getField(resourceName).getInt(null);
    }

    /**
     * Get text from a specific view
     * @param layout
     * @param index
     * @return
     */
    private ArrayList<String> getTextArray;
    public String[] getText(Class layout, int index) {
        ViewGroup baseView = (ViewGroup)getView(layout, index);
        getTextArray = new ArrayList<String>();
        getTextRecurViewGroup(baseView);

        return (String[])getTextArray.toArray(new String[getTextArray.size()]);
    }

    public String[] getTextFromView(View view) {
        ViewGroup baseView = (ViewGroup)view;
        getTextArray = new ArrayList<String>();
        getTextRecurViewGroup(baseView);

        return (String[])getTextArray.toArray(new String[getTextArray.size()]);
    }
    
    private void getTextRecurViewGroup(ViewGroup view) {
        for (int x = 0; x < view.getChildCount(); x++) {
            if (view.getChildAt(x) instanceof LinearLayout
                || view.getChildAt(x) instanceof RelativeLayout
                || view.getChildAt(x) instanceof FrameLayout) {
                getTextRecurViewGroup((ViewGroup)view.getChildAt(x));
            } else {
                // it's a view
                getTextRecurView((View)view.getChildAt(x));
            }
        }
    }

    private void getTextRecurView(View view) {
        // try to cast this to a text view
        try {
            TextView tview = (TextView) view;
            if(isVisible(tview))
                getTextArray.add(tview.getText().toString());
        } catch (Exception e) {
            // we'll just swallow this.. doesn't matter
        }
    }

    public boolean isVisible(View view) {
        // this will recurse up the parents to see if any parent view does not have a visibility of VISIBLE
        View currentView = view;
        boolean visibility = (currentView.getVisibility() == View.VISIBLE);

        while (currentView != null && visibility) {
            try {
                currentView = (View)currentView.getParent();
                visibility = (currentView.getVisibility() == View.VISIBLE);
            } catch (Exception e) {
                // we've hit something that isn't a view.. abort and return the visibility to the best of our knowledge
                break;
            }
        }

        return visibility;
    }

    /**
     * Gets the location on screen of a view and returns (x,y) as an int array
     * @param view
     * @return
     */
    public int[] getLocationOnScreen(View view) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);

        return viewLocation;
    }
}
