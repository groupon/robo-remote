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

public class Constants {
    // response constants
    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAILED = "FAILED";
    public static final String RESULT_OUTCOME = "outcome";
    public static final String RESULT_RESULTS = "results";
    public static final String RESULT_REASON = "reason";

    // request constants
    public static final String REQUEST_FIELD = "field";
    public static final String REQUEST_OPERATION = "operation";
    public static final String REQUEST_OPERATIONS = "operations";
    public static final String REQUEST_METHOD_NAME = "method_name";
    public static final String REQUEST_ARGUMENTS = "arguments";
    public static final String REQUEST_QUERY = "query";
    public static final String REQUEST_STORE = "store";
    public static final String REQUEST_REMOVE = "remove";
    public static final String REQUEST_RETRIEVE = "retrieve";
    public static final String REQUEST_MAP = "map";
    public static final String REQUEST_INSTANTIATE = "instantiate";
    public static final String REQUEST = "request";
    public static final String REQUEST_HEARTBEAT = "heartbeat";

    // robotium constants
    public static final String ROBOTIUM_SOLO = "solo";

    // nanohttpd constants
    public static final String NANO_POST = "POST";
    public static final String NANO_GET = "GET";

    // roboremote constants
    public static final String TYPE_EQUIVALENT_IDENTIFIER = "TYPE";

    public static final String RETURN_TYPE_LIST = "List";
    public static final String RETURN_TYPE_JAVA_UTIL = "java.util";
    public static final String RETURN_TYPE_BOOLEAN = "Boolean";
    public static final String RETURN_TYPE_ARRAY = "class [";
    public static final String RETURN_TYPE_VOID = "void";

    public static final String CLASS_TYPE_STATIC = "java.lang.Class";

    public static final String ARGUMENT_TYPE_STRING = "String";
    public static final String ARGUMENT_TYPE_CLASS = ".Class";
    public static final String ARGUMENT_TYPE_VIEW = ".View";
    public static final String ARGUMENT_TYPE_WIDGET = ".widget.";

    public static final String STORED = "STORED_";
}