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

package com.groupon.roboremote.roboremoteclientcommon;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.util.LinkedHashMap;

public class QueryBuilder {
    JSONObject request = new JSONObject();

    String queryStringRepresentation = "";
    protected int port = 8080;

    public QueryBuilder(int port) {
        this.port = port;
        request.put(Constants.REQUEST_OPERATIONS, new JSONArray());
    }

    /**
     * Used to get the value from a field
     * @param query
     * @param field_name
     * @return
     * @throws Exception
     */
    public QueryBuilder mapField(String query, String field_name) throws Exception {
        JSONObject op = new JSONObject();

        if (query != null)
            op.put(Constants.REQUEST_QUERY, query);

        op.put(Constants.REQUEST_FIELD, field_name);

        JSONArray operations = (JSONArray) request.get(Constants.REQUEST_OPERATIONS);
        operations.put(op);
        request.remove(Constants.REQUEST_OPERATIONS);
        request.put(Constants.REQUEST_OPERATIONS, operations);

        return this;
    }

    /**
     * Used to get the return from a method call
     * @param query
     * @param method_name
     * @param items
     * @return
     * @throws Exception
     */
    public QueryBuilder map(String query, String method_name, Object... items) throws Exception {
        JSONObject op = new JSONObject();
        if (query != null)
            op.put(Constants.REQUEST_QUERY, query);

        java.util.Map<String, Object> operation = new LinkedHashMap<String, Object>();
        operation.put(Constants.REQUEST_METHOD_NAME, method_name);

        if (query != null)
            queryStringRepresentation += query;

        queryStringRepresentation += "." + method_name;

        operation.put(Constants.REQUEST_ARGUMENTS, buildArgsArray(items));
        op.put(Constants.REQUEST_OPERATION, operation);

        JSONArray operations = (JSONArray) request.get(Constants.REQUEST_OPERATIONS);
        operations.put(op);
        request.remove(Constants.REQUEST_OPERATIONS);
        request.put(Constants.REQUEST_OPERATIONS, operations);

        return this;
    }

    /**
     * Used to instantiate a class
     * @param query
     * @param items
     * @return
     * @throws Exception
     */
    public QueryBuilder instantiate(String query, Object... items) throws Exception {
        JSONObject op = new JSONObject();
        if (query != null)
            op.put(Constants.REQUEST_INSTANTIATE, query);

        if (query != null)
            queryStringRepresentation += query;

        op.put(Constants.REQUEST_ARGUMENTS, buildArgsArray(items));

        JSONArray operations = (JSONArray) request.get(Constants.REQUEST_OPERATIONS);
        operations.put(op);
        request.remove(Constants.REQUEST_OPERATIONS);
        request.put(Constants.REQUEST_OPERATIONS, operations);

        return this;
    }

    /**
     * Internal function used to build a JSON Array of arguments from a list of items
     * @param items
     * @return
     * @throws Exception
     */
    private JSONArray buildArgsArray(Object ... items) throws Exception {
        JSONArray args = new JSONArray();
        for (int i = 0; i < items.length; i++) {

            if (items[i] instanceof java.lang.String)
            {
                args.put((String) items[i]);
            }
            else if (items[i] instanceof Number)
            {
                args.put((Number) items[i]);
            }
            else if (items[i] instanceof java.lang.Boolean)
            {
                args.put((Boolean) items[i]);
            }
            else if (items[i] == null)
            {
                args.put((Object)null);
            }
            else
            {
                throw new Exception("Invalid type");
            }
        }
        return args;
    }

    /**
     * Builder function to call a method on the result of a previous build function(ex: instantiate, map, retrieveresult)
     * @param method_name
     * @param items
     * @return
     * @throws Exception
     */
    public QueryBuilder call(String method_name, Object ... items) throws Exception {
        return map(null, method_name, items);
    }

    /**
     * Builder function to call a field(ex System.out)
     * @param fieldName
     * @return
     * @throws Exception
     */
    public QueryBuilder callField(String fieldName) throws Exception {
        return mapField(null, fieldName);
    }

    public String toString() {
        return request.toString();
    }

    /**
     * Store the result of the previous build operation.  The return value of this is the stored item
     * @param label
     * @return
     * @throws Exception
     */
    public QueryBuilder storeResult(String label) throws Exception {
        return genericRequest(Constants.REQUEST_STORE, label);
    }

    /**
     * Remove a stored value
     * @param label
     * @return
     * @throws Exception
     */
    public QueryBuilder removeResult(String label) throws Exception {
        return genericRequest(Constants.REQUEST_REMOVE, label);
    }

    /**
     * Retrieve a stored value.  This can be build upon using the call or callField builders
     * @param label
     * @return
     * @throws Exception
     */
    public QueryBuilder retrieveResult(String label) throws Exception {
        return genericRequest(Constants.REQUEST_RETRIEVE, label);
    }

    /**
     * Creates a key value request
     * @param type
     * @param value
     * @return
     * @throws Exception
     */
    private QueryBuilder genericRequest(String type, String value) throws Exception {
        JSONObject op = new JSONObject();
        op.put(type, value);
        JSONArray operations = (JSONArray) request.get(Constants.REQUEST_OPERATIONS);
        operations.put(op);
        request.remove(Constants.REQUEST_OPERATIONS);
        request.put(Constants.REQUEST_OPERATIONS, operations);
        return this;
    }

    /**
     * Execute a series of commands
     * @return
     * @throws Exception
     */
    public JSONArray execute() throws Exception {
        try {
            JSONArray retVal = Client.getInstance(port).map(toString());

            return retVal;
        } catch (Exception e) {
            throw new Exception(queryStringRepresentation + ": " + e.getMessage());
        }
    }

    /**
     * Helper function if a stored value needs to be as an argument to a builder function
     * Ex: .map("solo", "click", QueryBuilder.getStoredValue("myStoredValue")).execute()
     * @param label
     * @return
     */
    public static String getStoredValue(String label) {
        return Constants.STORED + label;
    }
}
