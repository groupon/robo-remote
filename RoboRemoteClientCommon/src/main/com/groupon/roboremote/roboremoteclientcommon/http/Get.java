package com.groupon.roboremote.roboremoteclientcommon.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: davidv
 * Date: 10/15/13
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Get {
    public Get() {

    }

    public static String get(String baseurl, String verb, String params) throws Exception {
        String response = "";

        URL oracle = new URL(baseurl + "/" + verb + "?" + params);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response += inputLine;
        in.close();

        return response;
    }
}
