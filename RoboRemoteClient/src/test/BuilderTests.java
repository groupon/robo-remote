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

import com.groupon.roboremote.roboremoteclient.QueryBuilder;
import org.json.*;
import org.junit.Test;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;

public class BuilderTests {
    @Test
    public void testMap() {
        try {
            QueryBuilder builder = new QueryBuilder();
            builder.map("solo", "blah", 1, true, "string");

            // take the string representation of builder and parse it as JSON
            JSONObject obj = new JSONObject(builder.toString());
            assertTrue(inspectOperationObject(obj.getJSONArray("operations").getJSONObject(0).getJSONObject("operation"), "blah", 1, true, "string"));
        } catch (Exception e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    public void testMapCall() {
        try {
            QueryBuilder builder = new QueryBuilder();
            builder.map("solo", "blah", 1, true, "string").call("blah2", "string", 2, false);

            // take the string representation of builder and parse it as JSON
            JSONObject obj = new JSONObject(builder.toString());
            assertTrue(inspectOperationObject(obj.getJSONArray("operations").getJSONObject(0).getJSONObject("operation"), "blah", 1, true, "string"));
            assertTrue(inspectOperationObject(obj.getJSONArray("operations").getJSONObject(1).getJSONObject("operation"), "blah2", "string", 2, false));
        } catch (Exception e) {
            fail("Error: " + e.getMessage());
        }
    }

    public static boolean inspectOperationObject(JSONObject operation, String method_name, Object ... params) throws Exception {
        // check method_name
        if (! method_name.equals(operation.getString("method_name"))) {
            return false;
        }

        // check arguments array
        int x = 0;
        for (Object param : params) {
            if (! param.equals(operation.getJSONArray("arguments").get(x))) {
                return false;
            }
            x++;
        }

        return true;
    }
}
