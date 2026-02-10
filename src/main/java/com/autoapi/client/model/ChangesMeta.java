package com.autoapi.client.model;

import com.google.gson.annotations.SerializedName;

/**
 * Pagination metadata for changes feed.
 */
public class ChangesMeta {

    @SerializedName("cur_change_id")
    private int curChangeId;

    @SerializedName("next_change_id")
    private int nextChangeId;

    private int limit;

    public int getCurChangeId() {
        return curChangeId;
    }

    public int getNextChangeId() {
        return nextChangeId;
    }

    public int getLimit() {
        return limit;
    }
}
