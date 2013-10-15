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

package com.groupon.roboremote.roboremoteclientcommon.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class LogEvent {
    String orignalString = null;
    boolean parsed = false;

    long timeMs = 0;
    String timeString = null;
    String verbosity = null;
    String tag = null;
    int pid = 0;
    String msg = null;

    // Lazy factory design pattern
    // http://en.wikipedia.org/wiki/Lazy_initialization
    public LogEvent(String s) {
        orignalString = s;
    }

    public long getTimeMs() {
        checkInit();
        return timeMs;
    }

    public long peekTimeMS()
    {
        // Time is always formattted as such: MM-DD HH:MM:SS.XXX
        return getTimeMs();
    }

    public String getTimeString() {
        checkInit();
        return timeString;
    }

    public String getVerbosity() {
        checkInit();
        return verbosity;
    }

    public String getTag() {
        checkInit();
        return tag;
    }

     public int getPid() {
        checkInit();
        return pid;
    }

    public String getMsg() {
        checkInit();
        return msg;
    }

    /**
     * Checks to see if the event message contains a substring
     * @param check
     * @return
     */
    public Boolean checkMsgSubstring(String check)
    {
        int index = orignalString.indexOf(check);
        boolean isSubstring = (index >= 0);
        return isSubstring;
    }

    public String toString()
    {
        checkInit();
        return String.format("%s %s/%s(%d): %s", timeString, verbosity, tag, pid, msg);
    }

    private void checkInit()
    {
        if(!parsed)
        {
            parseEvent();
            parsed = true;
        }
    }

    private void parseEvent()
    {
        try{
        String [] splits = orignalString.split("[ ]+");
        // Sometimes the PID is of format ( XX) instead of (XX)
        if(splits[2].indexOf(")") < 0)
        {
            splits[2] += splits[3];
            ArrayList<String> formatted = new ArrayList<String>(Arrays.asList(splits));
            formatted.remove(3);
            splits = formatted.toArray(new String[formatted.size()]);
        }

        // Parse time
        timeMs = parseTime(((new Date()).getYear() + 1900) + "-" + splits[0], splits[1]);
        timeString = String.format("%s-%s %s", ((new Date()).getYear() + 1900), splits[0], splits[1]);
        // Parse tag
        verbosity = parseVerbosity(splits[2]);
        tag = parseTag(splits[2]);
        pid = parsePid(splits[2]);
        // Parse message (may be empty)
        if (splits.length > 3) {
            msg = orignalString.substring(orignalString.indexOf(splits[3]));
        } else {
            msg = "";
        }
        }
        catch (Exception e)
        {
            // TODO: there are some strangely formated events in the system. need to deal with these?
        }
    }

    /**
     * Time comes in following format: 08-11 20:03:17.182:
     * Parse into milliseconds
     * @param day string of format 08-11
     * @param hours string of format 20:03:17.182:
     * @return
     */
    private long parseTime(String day, String hours)
    {
        /*
        Time timeToSet = new Time();
        Time currentTime = new Time();
        currentTime.setToNow();

        // Parse fields
        String[] daySplits = day.split("-");
        if(daySplits.length < 2)
            return 0;

        String[] hourSplits = hours.split(":");
        if(hourSplits.length < 2)
            return 0;

        String[] secondSplits = hourSplits[2].split("\\.");
        if(secondSplits.length < 2)
            return 0;

        int _year = currentTime.year;
        int _month = Integer.parseInt(daySplits[0])-1;
        int _day = Integer.parseInt(daySplits[1]);
        int _hour = Integer.parseInt(hourSplits[0]);
        int _min = Integer.parseInt(hourSplits[1]);
        int _sec = Integer.parseInt(secondSplits[0]);
        int _mili = Integer.parseInt(secondSplits[1]);

        //set(int second, int minute, int hour, int monthDay, int month, int year)
        timeToSet.set(_sec, _min, _hour, _day, _month, _year);

        // return calculated value
        long parsedTimeInMili = timeToSet.toMillis(true) + (long)_mili;
        return parsedTimeInMili;*/

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date utcDate = new Date();
        try {
            utcDate = format.parse(day.concat(" " + hours));
        } catch (Exception e) {

        }
        return utcDate.getTime();
    }

    private String parseVerbosity(String s)
    {
        return s.split("/")[0];
    }

    private String parseTag(String s)
    {
        int verbosityLength = parseVerbosity(s).length() +1;
        String tagPart = s.substring(verbosityLength);
        return tagPart.split("\\(")[0];
    }

    private int parsePid(String s)
    {
        try {
            String pidPart = s.split("\\(")[1];
            return Integer.parseInt(pidPart.split("\\)")[0]);
        } catch (Exception e) {
            e.toString();
        }
        return -1;
    }
}
