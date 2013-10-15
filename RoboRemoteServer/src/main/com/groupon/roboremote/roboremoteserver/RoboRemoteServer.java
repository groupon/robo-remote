package com.groupon.roboremote.roboremoteserver;

import android.app.Instrumentation;
import android.view.View;
import com.groupon.roboremote.roboremoteserver.robotium.Solo2;
import com.groupon.roboremote.roboremoteservercommon.RemoteServer;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: davidv
 * Date: 10/14/13
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoboRemoteServer extends RemoteServer {
    Solo2 solo = null;
    Instrumentation instrumentation = null;

    public RoboRemoteServer(Solo2 solo, Instrumentation instrumentation) {
        this.solo = solo;
        this.instrumentation = instrumentation;
    }

    /**
     * Get the instantiated solo if it is requested
     * @param query
     * @return
     */
    protected Object getInstantiatedClass(String query) {
        if (query.equals(Constants.ROBOTIUM_SOLO)) {
            return solo;
        }
        return null;
    }

    /**
     * Return a view with the specified name or null
     * @param viewName
     * @return
     */
    protected View getView(String viewName) {
        for (View view: solo.getCurrentViews()) {
            if (view.toString().contains(viewName)) {
                return view;
            }
        }

        return null;
    }

    /**
     * Get all type equivalents from the resource file
     * @param type
     * @return
     */
    protected String[] getTypeEquivalents(String type) {
        String[] returnArray = null;

        for (int x = 0; x < R.array.class.getFields().length; x++) {
            try {

                ArrayList<String> tmpArray = new ArrayList<String>();

                Collections.addAll(tmpArray, instrumentation.getContext().getResources().getStringArray(R.array.class.getFields()[x].getInt(null)));

                if (tmpArray.size() == 0)
                    continue;

                // safety precaution.. make sure the first item is "TYPE"
                if (tmpArray.get(0).compareTo(Constants.TYPE_EQUIVALENT_IDENTIFIER) != 0)
                    continue;

                tmpArray.remove(0);

                // look through the rest of the array and see if one of the types matches the type we passed in
                for (String tmpString: tmpArray) {
                    if (tmpString.compareTo(type) == 0) {
                        returnArray = tmpArray.toArray(new String[tmpArray.size()]);
                    }
                }

                if (returnArray != null)
                    break;

            } catch (Exception e) {
                // we'll get here if we ran out of types to look at
                break;
            }
        }

        return returnArray;
    }
}
