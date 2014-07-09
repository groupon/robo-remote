/*
        Copyright (c) 2014, Groupon, Inc.
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
package com.groupon.roboremote.roboremoteclientcommon.logging;

import com.android.ddmlib.MultiLineReceiver;
import com.groupon.roboremote.roboremoteclientcommon.DebugBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * This class represents a logcat logger thread which logs to a specified output file
 */
public class LogcatLogger {
    public static final Logger logger = LoggerFactory.getLogger(LogcatLogger.class);
    LogThread loggerThread = null;
    String _filename = null;

    public LogcatLogger(String filename) {
        _filename = filename;
    }

    public void startLogListener() throws Exception {
        // clear logcat first
        DebugBridge.get().clearLogCat();

        // start new listener
        logger.info("Starting log listener");
        loggerThread = new LogThread(_filename);
        loggerThread.start();
    }

    public void stopLogListener() throws Exception {
        logger.info("Stopping log listener");
        if (loggerThread != null) {
            loggerThread.close();
            loggerThread.interrupt();
        }
    }

    /**
     * Multi line receiver that writes to a file
     */
    public class MultiReceiver extends MultiLineReceiver {
        boolean closed = false;
        String _fileName = null;
        FileWriter fstream = null;
        BufferedWriter ostream = null;

        public MultiReceiver() {

        }

        public MultiReceiver(String outfile) throws Exception {
            _fileName = outfile;
            fstream = new FileWriter(_fileName);
            ostream = new BufferedWriter(fstream);
        }

        public void processNewLines(java.lang.String[] lines) {
            try {
                for (String line: lines) {
                    if (ostream != null) {
                        ostream.write(line + "\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        String _filename = "";
        MultiReceiver _receiver = null;

        public LogThread(String filename) {
            _filename = filename;
        }

        public void run() {
            try {
                logger.info("Logging to: {}", _filename);
                _receiver = new MultiReceiver(_filename);
                DebugBridge.get().runShellCommand("logcat -v time", _receiver, 0);
            } catch (Exception e) {
                logger.error("LogThread interrupted: {}", e);
            }

            logger.info("LogThread exited");
        }

        public void close() {
            // cause an exception in the receiver to kill the command
            _receiver.close();
        }
    }
}
