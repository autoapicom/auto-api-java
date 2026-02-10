package com.autoapi.client.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Query parameters for getOffers().
 * Uses fluent builder pattern — chain setters to build params.
 */
public class OffersParams {

    private int page;
    private String brand;
    private String model;
    private String configuration;
    private String complectation;
    private String transmission;
    private String color;
    private String bodyType;
    private String engineType;
    private Integer yearFrom;
    private Integer yearTo;
    private Integer mileageFrom;
    private Integer mileageTo;
    private Integer priceFrom;
    private Integer priceTo;

    /** Page number (required). */
    public OffersParams page(int page) { this.page = page; return this; }

    /** Filter by brand name. */
    public OffersParams brand(String brand) { this.brand = brand; return this; }

    /** Filter by model name. */
    public OffersParams model(String model) { this.model = model; return this; }

    /** Filter by configuration. */
    public OffersParams configuration(String configuration) { this.configuration = configuration; return this; }

    /** Filter by complectation. */
    public OffersParams complectation(String complectation) { this.complectation = complectation; return this; }

    /** Filter by transmission type. */
    public OffersParams transmission(String transmission) { this.transmission = transmission; return this; }

    /** Filter by color. */
    public OffersParams color(String color) { this.color = color; return this; }

    /** Filter by body type. */
    public OffersParams bodyType(String bodyType) { this.bodyType = bodyType; return this; }

    /** Filter by engine type. */
    public OffersParams engineType(String engineType) { this.engineType = engineType; return this; }

    /** Minimum year filter. */
    public OffersParams yearFrom(int yearFrom) { this.yearFrom = yearFrom; return this; }

    /** Maximum year filter. */
    public OffersParams yearTo(int yearTo) { this.yearTo = yearTo; return this; }

    /** Minimum mileage filter. */
    public OffersParams mileageFrom(int mileageFrom) { this.mileageFrom = mileageFrom; return this; }

    /** Maximum mileage filter. */
    public OffersParams mileageTo(int mileageTo) { this.mileageTo = mileageTo; return this; }

    /** Minimum price filter. */
    public OffersParams priceFrom(int priceFrom) { this.priceFrom = priceFrom; return this; }

    /** Maximum price filter. */
    public OffersParams priceTo(int priceTo) { this.priceTo = priceTo; return this; }

    /**
     * Converts parameters to a map of query string key-value pairs (snake_case keys).
     * Only non-null values are included.
     */
    public Map<String, String> toQueryParams() {
        Map<String, String> params = new LinkedHashMap<>();

        params.put("page", String.valueOf(page));

        if (brand != null) params.put("brand", brand);
        if (model != null) params.put("model", model);
        if (configuration != null) params.put("configuration", configuration);
        if (complectation != null) params.put("complectation", complectation);
        if (transmission != null) params.put("transmission", transmission);
        if (color != null) params.put("color", color);
        if (bodyType != null) params.put("body_type", bodyType);
        if (engineType != null) params.put("engine_type", engineType);
        if (yearFrom != null) params.put("year_from", yearFrom.toString());
        if (yearTo != null) params.put("year_to", yearTo.toString());
        if (mileageFrom != null) params.put("mileage_from", mileageFrom.toString());
        if (mileageTo != null) params.put("mileage_to", mileageTo.toString());
        if (priceFrom != null) params.put("price_from", priceFrom.toString());
        if (priceTo != null) params.put("price_to", priceTo.toString());

        return params;
    }
}
