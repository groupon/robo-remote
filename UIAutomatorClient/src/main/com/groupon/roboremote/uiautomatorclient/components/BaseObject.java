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

import com.groupon.roboremote.roboremoteclientcommon.Constants;
import com.groupon.roboremote.uiautomatorclient.QueryBuilder;
import org.json.JSONArray;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BaseObject {
    String storedId;

    protected BaseObject() throws Exception {
    }

    /**
     * Creates a new object based on the storedId of an object
     * @param storedId
     */
    public BaseObject(String storedId) {
        this.storedId = storedId;
    }

    // returns the stored ID for other operations to use
    protected String getStoredId() {
        return storedId;
    }

    /**
     * Call a function on this object
     * @param method
     * @param args
     * @return
     * @throws Exception
     */
    protected JSONArray callMethod(String method, Object ... args) throws Exception {
        return new QueryBuilder().retrieveResult(storedId).call(method, args).storeResult("LAST_" + getStoredId()).execute();
    }

    /**
     * Returns a stored ID for the last result of a call
     * @return
     * @throws Exception
     */
    public String getLastResult() throws Exception {
        return "LAST_" + getStoredId();
    }

    /**
     * Returns a stored value string for use with remote calls
     * This is useful when calling a function that takes a UiObject as an argument
     * @return
     */
    public String getStoredValue() {
        return QueryBuilder.getStoredValue(getStoredId());
    }

    /**
     * Returns a human readable representation of this
     * @return
     */
    public String toString() {
        try {
            String stringVal = new QueryBuilder().retrieveResult(storedId).call("toString").execute().getString(0);
            return stringVal;
        } catch (Exception e) {
            return "";
        }
    }
}
