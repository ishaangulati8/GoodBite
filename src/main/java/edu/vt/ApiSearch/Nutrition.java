/*
 * Created by Ishaan Gulati on 2021.12.2
 * Copyright © 2021 Ishaan Gulati. All rights reserved.
 */


package edu.vt.ApiSearch;

public class Nutrition {
    // Instance Variables.
    private String nutrients;
    private Double calories;
    private Double totalWeight;

    // Constructor
    public Nutrition(String nutrients, Double calories, Double totalWeight) {
        this.nutrients = nutrients;
        this.calories = calories;
        this.totalWeight = totalWeight;
    }

    // getters and setters
    public String getNutrients() {
        return nutrients;
    }

    public void setNutrients(String nutrients) {
        this.nutrients = nutrients;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }
}
