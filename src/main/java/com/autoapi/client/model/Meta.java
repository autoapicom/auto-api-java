package com.autoapi.client.model;

import com.google.gson.annotations.SerializedName;

/**
 * Pagination metadata for offers.
 */
public class Meta {

    private int page;

    @SerializedName("next_page")
    private int nextPage;

    private int limit;

    public int getPage() {
        return page;
    }

    public int getNextPage() {
        return nextPage;
    }

    public int getLimit() {
        return limit;
    }
}
