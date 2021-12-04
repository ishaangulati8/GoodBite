/*
 * Created by Ishaan Gulati on 2021.11.21
 * Copyright © 2021 Ishaan Gulati. All rights reserved.
 */

package edu.vt.ApiSearch;

// Creating object for nearby search stores api call.
// data returned from api is:
//String name, formatted, categories, plcaeId, distance, place_id, double lat, double lng
public class SearchedStore {
    private String name = null;
    private String formattedAddress = null;
    private String placeId = null;
    private Double distance = null;
    private String city = null;
    private String state = null;
    private String postCode = null;

    public SearchedStore(String name, String formattedAddress, String placeId, Double distance, String city, String state, String postCode) {
        this.name = name;
        this.formattedAddress = formattedAddress;
        this.placeId = placeId;
        this.distance = distance;
        this.city = city;
        this.state = state;
        this.postCode = postCode;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setSostCode(String postCode) {
        this.postCode = postCode;
    }
}
