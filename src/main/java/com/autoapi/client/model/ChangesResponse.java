package com.autoapi.client.model;

import java.util.List;

/**
 * Response from getChanges().
 */
public class ChangesResponse {

    private List<ChangeItem> result;
    private ChangesMeta meta;

    public List<ChangeItem> getResult() {
        return result;
    }

    public ChangesMeta getMeta() {
        return meta;
    }
}
