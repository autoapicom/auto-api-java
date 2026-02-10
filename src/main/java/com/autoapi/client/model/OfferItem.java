package com.autoapi.client.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * A single item in the offers result array.
 * Data is a JsonElement because the structure varies between sources.
 */
public class OfferItem {

    private int id;

    @SerializedName("inner_id")
    private String innerId;

    @SerializedName("change_type")
    private String changeType;

    @SerializedName("created_at")
    private String createdAt;

    private JsonElement data;

    public int getId() {
        return id;
    }

    public String getInnerId() {
        return innerId;
    }

    public String getChangeType() {
        return changeType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Raw JSON data. Structure varies between sources.
     * Use {@code new Gson().fromJson(item.getData(), YourClass.class)} to deserialize.
     */
    public JsonElement getData() {
        return data;
    }
}
