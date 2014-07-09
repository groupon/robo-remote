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

package com.groupon.roboremote.roboremoteclientcommon.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmSingleton {
    private static final Logger logger = LoggerFactory.getLogger(EmSingleton.class);

    /* Here is the instance of the Singleton */
    private static EventManager instance_ = null;

    /* Need the following object to synchronize */
    /* a block */
    private static Object syncObject_ = new Object();

    /* Prevent direct access to the constructor */
    private EmSingleton() {
        super();
    }

    private static void intialize() throws Exception {
        synchronized (syncObject_) {
            instance_ = new EventManager();
        }
    }

    public static EventManager get() throws Exception {
        if (instance_ == null) {
            intialize();
        }
        return instance_;
    }

    public static void release() {
        synchronized (syncObject_) {
            try {
                instance_.stopLogListener();
            } catch (Exception e) {
            }
            instance_ = null;
        }
    }
}