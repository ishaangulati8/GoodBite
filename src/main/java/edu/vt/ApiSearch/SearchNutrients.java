/*
 * Created by Ishaan Gulati on 2021.12.2
 * Copyright © 2021 Ishaan Gulati. All rights reserved.
 */

package edu.vt.ApiSearch;

import edu.vt.globals.Constants;
import edu.vt.globals.Methods;
import org.primefaces.shaded.json.JSONObject;

import java.util.Iterator;

public class SearchNutrients {

    //    https://api.edamam.com/api/nutrition-data?app_id=764889db&app_key=54fbdd1873720b7dc5c01d4eb508a1a7&nutrition-type=cooking&ingr=100g%20rice
    private String query;

    public SearchNutrients(String query) {
        this.query = query;
    }

    public Nutrition getNutrition() throws Exception {
        // Exceptions would be handled in the parent (calling scope) try catch block.
        String searchQuery = query.replaceAll(" ", "%20");
        //    https://api.edamam.com/api/nutrition-data?app_id=764889db&app_key=54fbdd1873720b7dc5c01d4eb508a1a7&nutrition-type=cooking&ingr=100g%20rice
        String apiRequestUrl = Constants.EDAMAM_NUTRITION_BASE_URL + "&app_id=" + Constants.EDAMAM_NUTRITION_APP_ID + "&api_key=" + Constants.EDAMAM_NUTRITION_API_KEY + "&ingr=" + query;
        // Obtain the JSON file (String of characters) containing the search results
        // The readUrlContent() method is given below
        String searchResultsJsonData = Methods.readUrlContent(apiRequestUrl);
        // The file returned from the API is in the form of a JSON object
        // Create a new JSON object from the returned file
        JSONObject searchResultsJsonObject = new JSONObject(searchResultsJsonData);
        Double calories = searchResultsJsonObject.optDouble("calories", 0);
        calories = round(calories);
        Double totalWeight = searchResultsJsonObject.optDouble("totalWeight", 0);
        totalWeight = round(totalWeight);
        String nutrients = "";
        boolean isNutrientsEmpty = true;
        JSONObject totalNutrients = searchResultsJsonObject.optJSONObject("totalNutrients");
        // iterator for iterating over the keys of the object.
        Iterator<String> keys = totalNutrients.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (totalNutrients.get(key) instanceof JSONObject) {
                JSONObject nutrient = new JSONObject(totalNutrients.get(key).toString());
                String label = nutrient.optString("label", "");
                if (label.equals("")) {
                    continue;
                }
                Double quantity = nutrient.optDouble("quantity", 0.00);
                quantity = round(quantity);
                String unit = nutrient.optString("unit", "");
                if (unit.equals("")) {
                    continue;
                }
                String temp = "";
                if (isNutrientsEmpty) {
                    temp = label + ": " + quantity + unit;
                    isNutrientsEmpty = false;
                } else {
                    temp = ", " + label + ": " + quantity + unit;
                }
                nutrients = nutrients.concat(temp);
            }
        }
        nutrients += ".";

        return new Nutrition(nutrients, calories, totalWeight);

    }

    private Double round(Double value) {
        value = value * 100;
        value = (double) Math.round(value);
        value = value / 100;
        return value;
    }


}

