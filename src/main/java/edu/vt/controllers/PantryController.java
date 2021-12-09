

/*
 * Created by Team 5 on 2021.11.15
 * Copyright © 2021 Team 5. All rights reserved.
 */

package edu.vt.controllers;

import edu.vt.ApiSearch.Nutrition;
import edu.vt.ApiSearch.SearchNutrients;
import edu.vt.EntityBeans.User;
import edu.vt.EntityBeans.UserPantry;
import edu.vt.FacadeBeans.UserPantryFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.globals.Methods;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
---------------------------------------------------------------------------
The @Named (javax.inject.Named) annotation indicates that the objects
instantiated from this class will be managed by the Contexts and Dependency
Injection (CDI) container. The name "pantryController" is used within
Expression Language (EL) expressions in JSF (XHTML) facelets pages to
access the properties and invoke methods of this class.
---------------------------------------------------------------------------
 */
@Named("pantryController")

/*
The @SessionScoped annotation preserves the values of the pantryController
object's instance variables across multiple HTTP request-response cycles
as long as the user's established HTTP session is alive.
 */
@SessionScoped

/*
-----------------------------------------------------------------------------
Marking the pantryController class as "implements Serializable" implies that
instances of the class can be automatically serialized and deserialized.

Serialization is the process of converting a class instance (object)
from memory into a suitable format for storage in a file or memory buffer,
or for transmission across a network connection link.

Deserialization is the process of recreating a class instance (object)
in memory from the format under which it was stored.
-----------------------------------------------------------------------------
 */
public class PantryController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
    */

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    PantryFacade bean into the instance variable 'pantryFacade' after it is instantiated at runtime.
     */
    @EJB
    private UserPantryFacade pantryFacade;

    // List of object references of Ingredient objects
    private List<UserPantry> listOfIngredients = null;

    // selected = object reference of a selected Ingredient object
    private UserPantry selected;
    private List<String> listOfIngredientNames = null;
    private List<String> selectedListOfIngredients = null;

    public void setListOfIngredients(List<UserPantry> listOfIngredients) {
        this.listOfIngredients = listOfIngredients;
    }

    public UserPantry getSelected() {
        return selected;
    }

    public void setSelected(UserPantry selected) {
        this.selected = selected;
    }

    public List<String> getSelectedListOfIngredients() {
        return selectedListOfIngredients;
    }

    public void setSelectedListOfIngredients(List<String> selectedListOfIngredients) {
        this.selectedListOfIngredients = selectedListOfIngredients;
    }

    public List<String> getListOfIngredientNames() {
        if (listOfIngredients == null) {
            User user = this.getLoggedInUser();
            Integer userId = user.getId();
            listOfIngredients = pantryFacade.findUserPantryByUserId(userId);
            listOfIngredientNames = new ArrayList<>();
            for (UserPantry ingredient : listOfIngredients) {
                listOfIngredientNames.add(ingredient.getIngredient());
            }
        }
        return listOfIngredientNames;
    }

    public void setListOfIngredientNames(List<String> listOfIngredientNames) {
        this.listOfIngredientNames = listOfIngredientNames;
    }

    public List<UserPantry> getListOfIngredients() {
        // get Logged in user.
        // get check if the list is null or not
        // if null then fetch the results
        // else return the result.
        if (listOfIngredients == null) {
            User user = this.getLoggedInUser();
            Integer userId = user.getId();
            listOfIngredients = pantryFacade.findUserPantryByUserId(userId);
            listOfIngredientNames = new ArrayList<>();
            for (UserPantry ingredient : listOfIngredients) {
                listOfIngredientNames.add(ingredient.getIngredient());
            }
        }
        return listOfIngredients;
    }


    private User getLoggedInUser() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        User signedInUser = (User) sessionMap.get("user");
        return signedInUser;
    }

    public void unselect() {
        this.selected = null;
    }

    /*
    *****************************
    Prepare to Create a New UserPantryItem
    *****************************
    */
    public void prepareCreate() {
        /*
        Instantiate a new UserPantry object and store its object reference into
        instance variable 'selected'. The UserPantry class is defined in UserPantry.java
         */
        // add user id and details.
        selected = new UserPantry();
        selected.setUserId(this.getLoggedInUser());
    }

    public void create() {
        Methods.preserveMessages();

        persist(JsfUtil.PersistAction.CREATE, "Pantry Ingredient was Successfully Created!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The CREATE operation is successfully performed.
            selected = null;        // Remove selection
            listOfIngredients = null;    // Invalidate listOfIngredients to trigger re-query.
        }
    }

    /*
    *************************************
    UPDATE Selected UserPantry item in the Database
    *************************************
     */
    public void update() {
        Methods.preserveMessages();

        persist(JsfUtil.PersistAction.UPDATE, "Pantry Ingredient was Successfully Updated!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The UPDATE operation is successfully performed.
            selected = null;        // Remove selection
            listOfIngredients = null;    // Invalidate listOfIngredients to trigger re-query.
        }
    }

    /*
     *************************************
     *   Cancel and Display List.xhtml   *
     *************************************
     */
    public String cancel() {
        // Unselect previously selected video object if any
        selected = null;
        return "/userPantry/ListUserPantry?faces-redirect=true";
    }
    /*
    ***************************************
    DELETE Selected UserPantry from the Database
    ***************************************
     */


    public void destroy() {
        Methods.preserveMessages();

        persist(JsfUtil.PersistAction.DELETE, "User Pantry Item was Successfully Deleted!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selected = null;        // Remove selection
            listOfIngredients = null;    // Invalidate listOfIngredients to trigger re-query.
        }
    }

    /**
     * @param persistAction  refers to CREATE, UPDATE (Edit) or DELETE action
     * @param successMessage displayed to inform the user about the result
     */
    private void persist(JsfUtil.PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != JsfUtil.PersistAction.DELETE) {
                    /*
                     -------------------------------------------------
                     Perform CREATE or EDIT operation in the database.
                     -------------------------------------------------
                     The edit(selected) method performs the SAVE (STORE) operation of the "selected"
                     object in the database regardless of whether the object is a newly
                     created object (CREATE) or an edited (updated) object (EDIT or UPDATE).

                     UserPantryFacade inherits the edit(selected) method from the AbstractFacade class.
                     */
                    String searchQuery = this.selected.getQuantity() + " " + this.selected.getUnit() + " " + this.selected.getIngredient();
                    SearchNutrients searchNutrients = new SearchNutrients(searchQuery);
                    Nutrition nutrient = searchNutrients.getNutrition();
                    this.selected.setCalories(nutrient.getCalories());
                    this.selected.setNutrients(nutrient.getNutrients());
                    pantryFacade.edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove(selected) method performs the DELETE operation of the "selected"
                     object in the database.

                     UserPantryFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    pantryFacade.remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, "A persistence error occurred.");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, "A persistence error occurred.");
            }
        }
    }

}









