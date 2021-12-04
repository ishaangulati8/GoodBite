/*
 * Created by Ishaan Gulati on 2021.11.21
 * Copyright © 2021 Ishaan Gulati. All rights reserved.
 */

package edu.vt.ApiSearch;

import edu.vt.EntityBeans.GeoCoding;
import edu.vt.FacadeBeans.GeoCodingFacade;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;
import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("nearByStoresSearchController")
@SessionScoped
public class NearByStoresSearchController implements Serializable {
    // Instance variables
    private List<SearchedStore> searchedStoreList = null;
    private String zipCode = null;
    private SearchedStore selected = null;
    private Double distance = null;
    private Integer maxNumberOfResults = null;
    @EJB
    private GeoCodingFacade geoCodingFacade;
    // Getters and Setters methods.

    public List<SearchedStore> getSearchedStoreList() {
        return searchedStoreList;
    }

    public void setSearchedStoreList(List<SearchedStore> searchedStoreList) {
        this.searchedStoreList = searchedStoreList;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public SearchedStore getSelected() {
        return selected;
    }

    public void setSelected(SearchedStore selected) {
        this.selected = selected;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getMaxNumberOfResults() {
        return maxNumberOfResults;
    }

    public void setMaxNumberOfResults(Integer maxNumberOfResults) {
        this.maxNumberOfResults = maxNumberOfResults;
    }

    // Instance Methods

    // clear search criterion
    public void clear() {
        this.distance = null;
        this.zipCode = null;
        this.maxNumberOfResults = null;
        this.selected = null;
    }

    // Search Stores.
    public String performSearch() {
        selected = null;
        // This sets the necessary flag to ensure the messages are preserved.
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        searchedStoreList = new ArrayList<>();
        try {
            List<GeoCoding> geoCodings = geoCodingFacade.findByZipCode(zipCode);
            if (geoCodings.size() == 0) {
                throw new ZipCodeNotFoundException("Zipcode Unavailable.");
            }
            GeoCoding geoCoding = geoCodings.get(0);
//            https://api.geoapify.com/v2/places?categories=commercial.supermarket&filter=circle:-80.4138877,37.2295834
//            ,5000&bias=proximity:-80.4138877,37.2295834&limit=20&apiKey=688f2afc8f1c4a45b76f18bdd8c286bf
            String apiUrl = Constants.GEOAPIFY_BASE_URL + geoCoding.getLng() + "," + geoCoding.getLat() + ","
                    + this.convertMileToMtr(this.distance) + "&bias=proximity:" + geoCoding.getLng() + "," + geoCoding.getLat()
                    + "&limit=" + this.maxNumberOfResults + "&apiKey=" + Constants.GEOAPIFY_API_KEY;
            // Obtain the JSON file (String of characters) containing the search results
            // The readUrlContent() method is given below
            String searchResultsJsonData = Methods.readUrlContent(apiUrl);
            // The file returned from the API is in the form of a JSON object
            // Create a new JSON object from the returned file
            JSONObject searchResultsJsonObject = new JSONObject(searchResultsJsonData);
            // Obtain the array of stores JSON objects from the key "hits"
            JSONArray jsonArrayFoundRecipes = searchResultsJsonObject.getJSONArray("features");
            int index = 0;
            int length = jsonArrayFoundRecipes.length();
            if (length > index) {
                while (index < length) {
                    // Get the JSONObject at index
                    JSONObject jsonObject = jsonArrayFoundRecipes.getJSONObject(index);
                    JSONObject foundStore = jsonObject.getJSONObject("properties");
                    String name = foundStore.optString("name", "");
                    if (name.equals("")) {
                        index++;
                        continue;
                    }
                    String formattedAddress = foundStore.optString("formatted", "");
                    if (formattedAddress.equals("")) {
                        index++;
                        continue;
                    }
                    String city = foundStore.optString("city", "");
                    if (city.equals("")) {
                        index++;
                        continue;
                    }
                    String state = foundStore.optString("state", "");
                    if (state.equals("")) {
                        index++;
                        continue;
                    }
                    String country = foundStore.optString("country", "");
                    if (country.equals("")){
                        index++;
                        continue;
                    }
                    int distanceInMeters = foundStore.optInt("distance", 0);
                    String postCode = foundStore.optString("postcode", "");
                    if (postCode.equals("")) {
                        index++;
                        continue;
                    }
                    String placesId = foundStore.optString("place_id", "");
                    if (placesId.equals("")) {
                        index++;
                        continue;
                    }
                    Double distanceInMiles = this.convertMetersToMile(distanceInMeters);
                    SearchedStore store = new SearchedStore(name, formattedAddress,placesId, distanceInMiles, city, state, postCode);
                    searchedStoreList.add(store);
                    index++;
                }
            } else {
                // No zipcode for the search query
                Methods.showMessage("Information", "No Results!", "No stores found for the search query!");
            }
        } catch (ZipCodeNotFoundException ex) {
            Methods.showMessage("Information", "Zipcode Unavailable!", "Zipcode not found in the database!");
            return "";
        } catch (Exception ex) {
            Methods.showMessage("Information", "No Results!", "No nearby stores found for the search query!");
        }
        return "/storeSearch/ApiSearchResults?faces-redirect=true";
    }

    private int convertMileToMtr(Double mile) {
        return (int) (mile * 1609.34);
    }

    private Double convertMetersToMile(int meters) {
        double miles = meters / 1609.34;
        miles = miles * 100;
        miles = (double) Math.round(miles);
        return miles / 100;
    }
}

//Custom exception class.
class ZipCodeNotFoundException extends RuntimeException {
    ZipCodeNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
