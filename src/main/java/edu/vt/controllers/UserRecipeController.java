/*
 * Created by Team 5 on 2021.12.4
 * Copyright © 2021 Team 5. All rights reserved.
 */

package edu.vt.controllers;

import edu.vt.EntityBeans.User;
import edu.vt.EntityBeans.UserRecipe;
import edu.vt.FacadeBeans.UserRecipeFacade;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/*
---------------------------------------------------------------------------
The @Named (javax.inject.Named) annotation indicates that the objects
instantiated from this class will be managed by the Contexts and Dependency
Injection (CDI) container. The name "userRecipeController" is used within
Expression Language (EL) expressions in JSF (XHTML) facelets pages to
access the properties and invoke methods of this class.
---------------------------------------------------------------------------
 */
@Named("userRecipeController")

/*
The @SessionScoped annotation preserves the values of the userRecipeController
object's instance variables across multiple HTTP request-response cycles
as long as the user's established HTTP session is alive.
 */
@SessionScoped

/*
-----------------------------------------------------------------------------
Marking the UserRecipeController class as "implements Serializable" implies that
instances of the class can be automatically serialized and deserialized.

Serialization is the process of converting a class instance (object)
from memory into a suitable format for storage in a file or memory buffer,
or for transmission across a network connection link.

Deserialization is the process of recreating a class instance (object)
in memory from the format under which it was stored.
-----------------------------------------------------------------------------
 */

public class UserRecipeController implements Serializable {
    List<UserRecipe> listOfRecipes = null;
    UserRecipe selected = null;

    @EJB
    private UserRecipeFacade userRecipeFacade;

    public List<UserRecipe> getListOfRecipes() {
        if (listOfRecipes == null) {
            User user = this.getLoggedInUser();
            listOfRecipes = userRecipeFacade.findUserRecipeByUserId(user.getId());
        }
        return listOfRecipes;
    }

    public void setListOfRecipes(List<UserRecipe> listOfRecipes) {
        this.listOfRecipes = listOfRecipes;
    }

    public UserRecipe getSelected() {
        return selected;
    }

    public void setSelected(UserRecipe selected) {
        this.selected = selected;
    }

    private User getLoggedInUser() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return (User) sessionMap.get("user");
    }

    public void unselect() {
        selected = null;
    }

    public String convertIntToString(Integer value) {
        return Integer.toString(value);
    }

    public String convertDoubleToString(Double value) {
        return Double.toString(value);
    }

    public void clear() {
        selected = null;
        listOfRecipes = null;
    }
}
