package com.autoapi.client.model;

import java.util.List;

/**
 * Response from getOffers() and getOffer().
 */
public class OffersResponse {

    private List<OfferItem> result;
    private Meta meta;

    public List<OfferItem> getResult() {
        return result;
    }

    public Meta getMeta() {
        return meta;
    }
}
