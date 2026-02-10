package com.autoapi.client.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Common offer data fields shared across all sources.
 * Since each source may have additional fields, use
 * {@code new Gson().fromJson(item.getData(), OfferData.class)} to deserialize,
 * or create your own class with source-specific fields.
 */
public class OfferData {

    @SerializedName("inner_id")
    private String innerId;

    private String url;
    private String mark;
    private String model;
    private String generation;
    private String configuration;
    private String complectation;
    private String year;
    private String color;
    private String price;

    @SerializedName("km_age")
    private String kmAge;

    @SerializedName("engine_type")
    private String engineType;

    @SerializedName("transmission_type")
    private String transmissionType;

    @SerializedName("body_type")
    private String bodyType;

    private String address;

    @SerializedName("seller_type")
    private String sellerType;

    @SerializedName("is_dealer")
    private boolean isDealer;

    private String displacement;

    @SerializedName("offer_created")
    private String offerCreated;

    private List<String> images;

    public String getInnerId() { return innerId; }
    public String getUrl() { return url; }
    public String getMark() { return mark; }
    public String getModel() { return model; }
    public String getGeneration() { return generation; }
    public String getConfiguration() { return configuration; }
    public String getComplectation() { return complectation; }
    public String getYear() { return year; }
    public String getColor() { return color; }
    public String getPrice() { return price; }
    public String getKmAge() { return kmAge; }
    public String getEngineType() { return engineType; }
    public String getTransmissionType() { return transmissionType; }
    public String getBodyType() { return bodyType; }
    public String getAddress() { return address; }
    public String getSellerType() { return sellerType; }
    public boolean isDealer() { return isDealer; }
    public String getDisplacement() { return displacement; }
    public String getOfferCreated() { return offerCreated; }
    public List<String> getImages() { return images; }
}
