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

package com.groupon.roboremote.roboremoteclient;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Exception;
import java.lang.Object;
import java.lang.String;
import java.lang.Thread;
import java.util.Date;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger("test");

    private static String API_URL = "http://localhost:8080";

    private static String TEST_NAME = new String();

    private static Client instance = null;

    protected Client() {
        // Exists only to defeat instantiation.
    }

    public static Client getInstance() {
        if(instance == null) {
            instance = new Client();
        }
        return instance;
    }

    /**
     * Returns true if there is RoboRemoteServer currently listening
     * @return
     * @throws Exception
     */
    public static boolean isListening() throws Exception {
        try {
            com.groupon.roboremote.roboremoteclient.Client.map(Constants.ROBOTIUM_SOLO, "waitForText", "Stuff", 1, 1000, false);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static JSONObject post_to_server(String verb, String postBody) throws Exception {
        //postBody = postBody.replace("%", "%%");

        String responseStr = com.groupon.roboremote.roboremoteclient.http.Post.post(API_URL, verb, "request=" + postBody);

        JSONObject response = new JSONObject(responseStr);
        return response;
    }

    public static JSONArray map(String requestJson) throws Exception {
        org.json.JSONObject result = post_to_server(Constants.REQUEST_MAP, requestJson);

        if (result.getString(Constants.RESULT_OUTCOME).compareTo(Constants.RESULT_SUCCESS) != 0) {
            throw new Exception("Client::map:: " + "failed because: " + result.getString(Constants.RESULT_REASON));
        }

        JSONArray results = new JSONArray();

        results = result.getJSONArray(Constants.RESULT_RESULTS);

        return results;
    }

    /**
     * Used to get the value from a field
     * @param query
     * @param field_name
     * @return
     * @throws Exception
     */
    public static JSONArray mapField(String query, String field_name) throws Exception {
        QueryBuilder builder = new QueryBuilder();
        builder.mapField(query, field_name);
        return builder.execute();
    }

    /**
     * Used to call a method with a list of arguments
     * @param query
     * @param method_name
     * @param items
     * @return
     * @throws Exception
     */
    public static JSONArray map(String query, String method_name, Object... items) throws Exception {
        QueryBuilder builder = new QueryBuilder();
        builder.map(query, method_name, items);
        return builder.execute();
    }

    /**
     * Touch an item with the specified text
     * @param text
     * @return
     */
    public static boolean touch(String text) {
        try {
            JSONArray views_touched = map(Constants.ROBOTIUM_SOLO, "clickOnText", text, 1, true);

            if (views_touched.length() == 0)
            {
                return false;
            }

            Thread.sleep(100);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Returns the current time as the device sees it
     * @return
     */
    public static Date getOSTime() throws Exception {
        return new Date(map("java.util.Date", "getTime").getLong(0));
    }
}
