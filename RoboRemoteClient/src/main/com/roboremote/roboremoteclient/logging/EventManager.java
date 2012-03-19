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

package com.roboremote.roboremoteclient.logging;

import com.android.ddmlib.MultiLineReceiver;
import com.roboremote.roboremoteclient.DebugBridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

public class EventManager {
    private ArrayList<String> args = null;
    private long startTime;
    private long lastEventTime;

    InputStream is = null;
    InputStreamReader isr = null;
    BufferedReader br = null;

    ArrayList<LogEvent> events = new ArrayList<LogEvent>();
    ArrayList<String> logLines = new ArrayList<String>();

    private LogThread loggerThread = null;

    public EventManager() throws Exception {
        args = new ArrayList<String>(Arrays.asList("-v", "time"));

        try {
            // start logcat
            startADB();
            gatherEvents();
            setCheckPoint();
        } catch (Exception e) {
            throw new Exception("Could not start event manager");
        }
    }

    public void setCheckPoint() throws Exception
    {
        gatherEvents();
        startTime = lastEventTime;

        // clear events list
        events.clear();

        TestLogger.get().info("Set checkpoint!");
    }

    public void startADB() throws Exception {
        startLogListener();

        Thread.sleep(1000);
    }

    /**
     * closes the file stream/adb
     */
    public void close() throws Exception{
        is = null;
        isr = null;
        br = null;

        // kill all logcats
        DebugBridge.get().stopLogListener();

        stopLogListener();
    }

    public LogEvent checkEvent(String filter) throws Exception {
        ArrayList<LogEvent> events = gatherEvents();

        for(LogEvent event: events){
            if(event.checkMsgSubstring(filter)){
                return event;
            }
        }

        return null;
    }

    public LogEvent waitForEvent(String filter, int timeout) throws Exception
    {
        return waitForEvent(filter, timeout, true);
    }

    public LogEvent waitForEvent(String filter, int timeout, boolean removeEvent) throws Exception
    {
        TestLogger.get().info(String.format("Waiting for event: %s...", filter.substring(0, filter.length()-1)));
        int retries = timeout/1000 == 0 ? 1 : timeout/1000;
        for(int i = 0; i < retries; i++)
        {
            LogEvent event = checkEvent(filter);
            if(event != null){
                TestLogger.get().info(String.format("Found event: %s", filter.substring(0, filter.length()-1)));

                if (removeEvent)
                    events.remove(event);

                return event;
            }
            Thread.sleep(1000);
        }
        return null;
    }

    public ArrayList<LogEvent> getEvents() {
        return gatherEvents();
    }

    public void clearEvents() throws Exception{
        events.clear();
    }

    protected ArrayList<LogEvent> gatherEvents() {
        final StringBuilder log = new StringBuilder();

        try {
            synchronized (logLines) {
                for (String line: logLines) {
                    // Add events to the events arraylist if they occur after the LogCollector has started to run
                    LogEvent event = new LogEvent(line);

                    if (event.peekTimeMS() > 0)
                        lastEventTime = event.peekTimeMS();

                    if(event.peekTimeMS() > startTime)
                    {
                        events.add(event);
                    }
                }

                // clear the queue
                logLines.clear();
            }
        } catch (Exception e) {
            TestLogger.get().error(String.format("gatherEvents doInBackground failed: %s", e.toString()));
        }

        return events;
    }

    public void startLogListener() throws Exception {
        loggerThread = new LogThread();
        loggerThread.start();
    }

    public void stopLogListener() throws Exception {
        if (loggerThread != null) {
            loggerThread.close();
            loggerThread.interrupt();
        }
    }

    /**
     * This is the multi line receiver for the log listener
     * It stores lines in an arraylist that are consumed by the event manager
     */
    public class MultiReceiver extends MultiLineReceiver {
        boolean closed = false;

        public MultiReceiver() {

        }

        public void processNewLines(java.lang.String[] lines) {
            try {
                synchronized (logLines) {
                    for(String line : lines) {
                        logLines.add(line);
                    }
                }
            } catch (Exception e) {

            }
        }

        public boolean isCancelled() {
            return closed;
        }

        public void close() {
            closed = true;
        }
    }

    /**
     * This thread contains the running the log listener
     * DebugBridge does not return until the logcat finishes(never) so we have to run it in its own thread
     */
    private class LogThread extends Thread {
        MultiReceiver _receiver = null;

        public LogThread() {
        }

        public void run() {
            try {
                _receiver = new MultiReceiver();
                DebugBridge.get().runShellCommand("logcat -c");
                DebugBridge.get().runShellCommand("logcat -v time", _receiver, 0);
            } catch (Exception e) {
                System.out.println("INTERRUPTED!!!");
            }
        }

        public void close() {
            // cause an exception in the receiver to kill the command
            _receiver.close();
        }
    }
}
