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

package com.groupon.roboremote.roboremoteclient.components;

import com.groupon.roboremote.roboremoteclient.QueryBuilder;
import com.groupon.roboremote.roboremoteclient.Solo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListView {
    private static final Logger logger = LoggerFactory.getLogger("test");

    public static void scrollToTop() throws Exception {
        scrollToTop(Solo.getCurrentListViews()[0]);
    }

    public static void scrollToTop(String listRef) throws Exception {
        int listViewIndex = getListViewIndex(listRef);

        // keep scrolling until this is true
        while (Solo.scrollUpList(listViewIndex)) {
        }
    }

    public static void scrollToBottom() throws Exception {
        scrollToTop(Solo.getCurrentListViews()[0]);
    }

    public static void scrollToBottom(String listRef) throws Exception {
        int listViewIndex = getListViewIndex(listRef);

        // keep scrolling until this is true
        while (Solo.scrollDownList(listViewIndex)) {
        }
    }

    public static void clickItemAtIndex(int itemIndex) throws Exception {
        clickItemAtIndex(Solo.getCurrentListViews()[0], itemIndex);
    }

    /**
     *
     * @param listRef - reference to the list.  Can be gotten using Solo.getCurrentListViews
     * @param itemIndex - index of the item to click
     */
    public static void clickItemAtIndex(String listRef, int itemIndex) throws Exception {
        int listViewIndex = getListViewIndex(listRef);

        Solo.clickInList(scrollToIndex(listRef,  itemIndex), listViewIndex);
    }

    public static int scrollToIndex(int itemIndex) throws Exception {
        return scrollToIndex(Solo.getCurrentListViews()[0], itemIndex);
    }

    /**
     * Scrolls the specified item index onto the screen and returns the offset based on the current visible list
     * @param listRef
     * @param itemIndex
     * @return
     * @throws Exception
     */
    public static int scrollToIndex(String listRef, int itemIndex) throws Exception {
        int listViewIndex = getListViewIndex(listRef);

        if (itemIndex >= numItemsInList(listRef))
            throw new Exception("Item index is greater than number of items in the list");

        // scroll to the top of this view if we already passed the index being looked for
        QueryBuilder builder = new QueryBuilder();
        int firstVisiblePosition = builder.map("solo", "getCurrentListViews").call("get", listViewIndex).call("getFirstVisiblePosition").execute().getInt(0);
        if (firstVisiblePosition > itemIndex)
            scrollToTop();

        boolean found = false;
        boolean scrollDown = true;
        // need to get the wanted item onto the screen
        while (! found) {
            builder = new QueryBuilder();
            firstVisiblePosition = builder.map("solo", "getCurrentListViews").call("get", listViewIndex).call("getFirstVisiblePosition").execute().getInt(0);

            builder = new QueryBuilder();
            int headersViewsCount = builder.map("solo", "getCurrentListViews").call("get", listViewIndex).call("getHeaderViewsCount").execute().getInt(0);

            firstVisiblePosition = firstVisiblePosition - headersViewsCount;

            builder = new QueryBuilder();
            int visibleChildCount = builder.map("solo", "getCurrentListViews").call("get", listViewIndex).call("getChildCount").execute().getInt(0);

            int wantedPosition = itemIndex - firstVisiblePosition;

            if (wantedPosition >= visibleChildCount) {
                scrollDown = Solo.scrollDownList(listViewIndex);
            } else {
                found = true;

                // return the position
                return wantedPosition;
            }
        }

        throw new Exception("Could not find item");
    }

    public static int numItemsInList() throws Exception {
        return numItemsInList(Solo.getCurrentListViews()[0]);
    }

    public static int numItemsInList(String listRef) throws Exception {
        int listViewIndex = getListViewIndex(listRef);

        QueryBuilder builder = new QueryBuilder();
        int childCount = builder.map("solo", "getCurrentListViews").call("get", listViewIndex).call("getAdapter").call("getCount").execute().getInt(0);

        return childCount;
    }

    private static int getListViewIndex(String listRef) throws Exception {
        String[] listViews = Solo.getCurrentListViews();

        if (listViews.length == 0)
            throw new Exception("No listviews on the screen");

        int index = 0;
        for (String listView : listViews) {
            if (listView.equals(listRef)) {
                return index;
            }
            index++;
        }

        throw new Exception("Could not find listView");
    }
    
    public static String[] getText(String listRef, int itemIndex) throws Exception {
        int listViewIndex = getListViewIndex(listRef);

        int actualIndex = scrollToIndex(listRef, itemIndex);

        String cellRef = new QueryBuilder().map("solo", "getCurrentListViews").call("get", listViewIndex).call("getChildAt", actualIndex).execute().getString(0);
        
        return Solo.getTextFromView(cellRef);
    }
}
