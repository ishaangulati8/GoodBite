/*
 * Created by Team 5 on 2021.7.14
 * Copyright © 2021 Team 5. All rights reserved.
 */
package edu.vt.misc;

import edu.vt.EntityBeans.User;
import edu.vt.globals.Constants;

import javax.persistence.Entity;
import java.io.Serializable;


public class SharedMealData  {

//    private static final long serialVersionUID = 1L;

    private String username;
    private User userId;
    private Integer id;
    private String mealName;
    private String mealPhoto;
    private String mealDescription;


    /*
    ===================================================================
    Class constructors for instantiating a UploadedMeal entity object to
    represent a row in the UploadedMeal table in the GoodBiteDB database.
    ===================================================================
     */
    public SharedMealData() {
    }

    // Used in handleFileUpload() method of FileUploadManager
    public SharedMealData(String username, User userId, Integer id, String mealName, String mealPhoto, String mealDescription) {

        this.username = username;
        this.userId = userId;
        this.id = id;
        this.mealName = mealName;
        this.mealPhoto = mealPhoto;
        this.mealDescription = mealDescription;
    }

    /*
    ======================================================
    Getter and Setter methods for the attributes (columns)
    of the UploadedMeal table in the GoodBiteDB database.
    ======================================================
     */

    public String getMealPhoto() {
        return mealPhoto;
    }

    public String getMealName() {
        return mealName;
    }

    public String getMealDescription() {
        return mealDescription;
    }

    public User getUserId() {
        return userId;
    }

    public String getUsername() { return username;}
    public Integer getId() {return id;}
    /*
    ================================
    Instance Methods Used Internally
    ================================
     */

    /*
    =============
    Other Methods
    =============
     */

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public void setMealPhoto(String mealPhoto) {
        this.mealPhoto = mealPhoto;
    }

    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    public String getFilePath() {
        return Constants.FILES_ABSOLUTE_PATH + getMealPhoto();
    }

}
