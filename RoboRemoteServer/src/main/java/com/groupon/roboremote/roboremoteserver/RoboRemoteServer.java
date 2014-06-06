package com.groupon.roboremote.roboremoteserver;

import android.app.Instrumentation;
import android.view.View;
import com.groupon.roboremote.roboremoteserver.robotium.Solo2;
import com.groupon.roboremote.roboremoteservercommon.RemoteServer;

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
}
