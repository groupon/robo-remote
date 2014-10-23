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

package com.groupon.roboremote.roboremoteclientcommon;

import com.groupon.roboremote.roboremoteclientcommon.http.Get;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Exception;
import java.lang.Object;
import java.lang.String;
import java.net.URLEncoder;
import java.util.Date;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger("test");

    private static String API_BASE_URL = "http://localhost";
    protected int API_PORT = com.groupon.roboremote.Constants.ROBOREMOTE_SERVER_PORT;

    private static Client instance = null;

    protected Client() {
        // Exists only to defeat instantiation.
    }

    protected static Client getInstance() {
        return instance;
    }

    protected static Client getInstance(int port) {
        if(instance == null) {
            instance = new Client();
        }

        // TODO: This is not safe
        instance.API_PORT = port;
        return instance;
    }

    /**
     * Returns true if there is RoboRemoteServer currently listening
     * @return
     * @throws Exception
     */
    public boolean isListening() throws Exception {
        try {
            JSONObject resp = new JSONObject(Get.get(API_BASE_URL + ":" + API_PORT, Constants.REQUEST_HEARTBEAT, ""));

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public JSONObject post_to_server(String verb, String postBody) throws Exception {
        String responseStr = com.groupon.roboremote.roboremoteclientcommon.http.Post.post(API_BASE_URL + ":" + API_PORT, verb, "request=" + URLEncoder.encode(postBody));

        return new JSONObject(responseStr);
    }

    public JSONArray map(String requestJson) throws Exception {
        org.json.JSONObject result = post_to_server(Constants.REQUEST_MAP, requestJson);
        if (result.getString(Constants.RESULT_OUTCOME).compareTo(Constants.RESULT_SUCCESS) != 0) {
            String reason = result.has(Constants.RESULT_REASON) ? result.getString(Constants.RESULT_REASON) : "No reason provided";
            throw new Exception("Client::map:: " + "failed because: " + reason);
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
    public JSONArray mapField(String query, String field_name) throws Exception {
        QueryBuilder builder = new QueryBuilder(API_PORT);
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
    public JSONArray map(String query, String method_name, Object... items) throws Exception {
        QueryBuilder builder = new QueryBuilder(API_PORT);
        builder.map(query, method_name, items);
        return builder.execute();
    }

    /**
     * Returns the current time as the device sees it
     * @return
     */
    public Date getOSTime() throws Exception {
        return new Date(map("java.util.Date", "getTime").getLong(0));
    }
}
