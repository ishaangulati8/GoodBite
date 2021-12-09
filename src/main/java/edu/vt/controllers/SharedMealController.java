/*
 * Created by Team 5 on 2021.11.15
 * Copyright © 2021 Team 5. All rights reserved.
 */

package edu.vt.controllers;

import edu.vt.EntityBeans.SharedMeal;
import edu.vt.EntityBeans.UploadedMeal;
import edu.vt.EntityBeans.User;
import edu.vt.FacadeBeans.SharedMealFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("sharedMealController")
@SessionScoped

public class SharedMealController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    SharedMealFacade bean into the instance variable 'sharedMealFacade' after it is instantiated at runtime.
     */
    @EJB
    private SharedMealFacade sharedMealFacade;

    // 'selected' contains the object reference of the selected User File object
    private SharedMeal selected;

    // 'listOfSharedMeals' is a List containing the object references of User File objects
    private List<SharedMeal> listOfSharedMeals = null;

    /*
    cleanedFileNameHashMap<KEY, VALUE>
        KEY   = Integer fileId
        VALUE = String cleanedFileNameForSelected
     */
    HashMap<Integer, String> cleanedFileNameHashMap = null;

    // Selected row number in p:dataTable in ListUserFiles.xhtml
    private String selectedRowNumber = "0";

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public SharedMeal getSelected() {
        return selected;
    }

    public void setSelected(SharedMeal selected) {
        this.selected = selected;
    }

    public String getSelectedRowNumber() {
        return selectedRowNumber;
    }

    public void setSelectedRowNumber(String selectedRowNumber) {
        this.selectedRowNumber = selectedRowNumber;
    }


    public List<SharedMeal> getListOfSharedMeals() {

        listOfSharedMeals = sharedMealFacade.findAll();

        // Instantiate a new hash map object
        cleanedFileNameHashMap = new HashMap<>();

        /*
        cleanedFileNameHashMap<KEY, VALUE>
            KEY   = Integer fileId
            VALUE = String cleanedFileNameForSelected
         */
        listOfSharedMeals.forEach(userFile -> {

            // Obtain the filename stored in CS5704-Team5-FileStorage/FileStorage as 'userId_filename'
            String storedFileName = userFile.getMealPhoto();

            // Remove the "userId_" (e.g., "4_") prefix in the stored filename
            String cleanedFileName = storedFileName.substring(storedFileName.indexOf("_") + 1);

            // Obtain the file database Primary Key id
            Integer fileId = userFile.getId();

            // Create an entry in the hash map as a key-value pair
            cleanedFileNameHashMap.put(fileId, cleanedFileName);
        });

        return listOfSharedMeals;
    }

    /*
    ================
    Instance Methods
    ================
     */

    public boolean isUserMeal(User mealOwner){
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        // false if not signed in
        if (sessionMap.get("username") == null) {
            System.out.println("false");
            return false;
        }


        User signedInUser = (User) sessionMap.get("user");
        System.out.println(signedInUser.equals(mealOwner));
        return signedInUser.equals(mealOwner);

    }

    /*
    **********************
    Create a New User File
    **********************
     */
    public void create() {
        persist(PersistAction.CREATE,"User File was Successfully Created!");
        /*
        JsfUtil.isValidationFailed() returns TRUE if the validationFailed() method has been called
        for the current request. Return of FALSE means that the create operation was successful and 
        we can reset listOfSharedMeals to null so that it will be recreated with the newly created user file.
         */
        if (!JsfUtil.isValidationFailed()) {
            selected = null;            // Remove selection
            listOfSharedMeals = null;     // Invalidate listOfSharedMeals to trigger re-query.
        }
    }

    // We do not allow update of a user file.
    public void update() {
        persist(PersistAction.UPDATE,"User File was Successfully Updated!");
    }

    public String deleteSharedFile(SharedMeal sharedMealToDelete) {

        /*
        We need to preserve the messages since we will redirect to show a
        different JSF page after successful deletion of the user file.
         */
        Methods.preserveMessages();

        try {

            // Delete the user file record from the database
            sharedMealFacade.remove(sharedMealToDelete);
            // UploadedMealFacade inherits the remove() method from AbstractFacade

            Methods.showMessage("Information", "Success!", "Selected File is Successfully Deleted!");

            // See method below
            refreshFileList();

            return "/sharedMeals/SharedMeals?faces-redirect=true";

        } catch (Exception ex) {
            Methods.showMessage("Fatal Error", "Something went wrong while deleting the meal!",
                    "See: " + ex.getMessage());
            return "";
        }
    }

    /*
     ****************************************************************************
     *   Perform CREATE, EDIT (UPDATE), and DELETE Operations in the Database   *
     ****************************************************************************
     */
    /**
     * @param persistAction refers to CREATE, UPDATE (Edit) or DELETE action
     * @param successMessage displayed to inform the user about the result
     */
    private void persist(PersistAction persistAction, String successMessage) {

        if (selected != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    /*
                     -------------------------------------------------
                     Perform CREATE or EDIT operation in the database.
                     -------------------------------------------------
                     The edit(selected) method performs the SAVE (STORE) operation of the "selected"
                     object in the database regardless of whether the object is a newly
                     created object (CREATE) or an edited (updated) object (EDIT or UPDATE).
                    
                     SharedMealFacade inherits the edit(selected) method from the AbstractFacade class.
                     */
                    sharedMealFacade.edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove method performs the DELETE operation in the database.
                    
                     SharedMealFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    sharedMealFacade.remove(selected);
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
                    JsfUtil.addErrorMessage(ex,"A Persistence Error Occurred!");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex,"A Persistence Error Occurred!");
            }
        }
    }

    public SharedMeal getUserFile(Integer id) {
        return sharedMealFacade.find(id);
    }

    @FacesConverter(forClass = SharedMeal.class)
    public static class UserFileControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SharedMealController controller = (SharedMealController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userFileController");
            return controller.getUserFile(getKey(value));
        }

        Integer getKey(String value) {
            int key;
            key = Integer.parseInt(value);
            return key;
        }

        String getStringKey(Integer value) {
            return String.valueOf(value);
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof SharedMeal) {
                SharedMeal o = (SharedMeal) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                        "object {0} is of type {1}; expected type: {2}",
                        new Object[]{object, object.getClass().getName(), SharedMeal.class.getName()});
                return null;
            }
        }
    }

    /*
    ========================
    Refresh User's File List
    ========================
     */
    public void refreshFileList() {
        /*
        By setting the listOfSharedMeals to null, we force the getListOfUserFiles
        method above to retrieve all of the user's files again.
         */
        selected = null;            // Remove selection
        listOfSharedMeals = null;     // Invalidate listOfSharedMeals to trigger re-query.
    }


    /*
    =====================================
    Return Cleaned Filename given File Id
    =====================================
     */
    public String cleanedFilenameForFileId(Integer fileId) {
        /*
        cleanedFileNameHashMap<KEY, VALUE>
            KEY   = Integer fileId
            VALUE = String cleanedFileNameForSelected
         */
        // Return the cleaned filename for the given fileId
        return cleanedFileNameHashMap.get(fileId);
    }

    /*
    =========================================
    Return Cleaned Filename for Selected File
    =========================================
     */
    // This method is called from UserFiles.xhtml by passing the fileId as a parameter.
    public String cleanedFileNameForSelected() {

        Integer fileId = selected.getId();
        /*
        cleanedFileNameHashMap<KEY, VALUE>
            KEY   = Integer fileId
            VALUE = String cleanedFileNameForSelected
         */

        // Return the cleaned filename for the selected file
        return cleanedFileNameHashMap.get(fileId);
    }

    /*
    ==========================
    Return Selected File's URI
    ==========================
     */
    public String selectedFileURI() {
        return Constants.FILES_URI + selected.getMealPhoto();
    }

    public String getFileURI(SharedMeal file){
        return Constants.FILES_URI + file.getMealPhoto();
    }

    /*
    =============================================
    Return True if Selected File is an Image File
    =============================================
    Any type of file can be uploaded or downloaded.
    We identify the types of image files that can be displayed in the web browser.
     */
    public boolean isImage() {

        switch (extensionOfSelectedFileInCaps()) {
            case "JPG":
            case "JPEG":
            case "PNG":
            case "GIF":
                // Selected file is an acceptable image file
                return true;
            default:
                return false;
        }
    }

    /*
    ========================================
    Return True if Selected File is Viewable
    ========================================
    Any type of file can be uploaded or downloaded.
    We identify the types of files that can be displayed in the web browser.
     */
    public boolean isViewable() {

        switch (extensionOfSelectedFileInCaps()) {
            case "HTML":
            case "PDF":
            case "TXT":
                // Selected file is viewable within the web browser
                return true;
            default:
                return false;
        }
    }

    /*
    =====================================
    The HTML5 <video> tag can play the
    following video files: MP4, OGG, WEBM
    =====================================
    Any type of file can be uploaded or downloaded.
    We identify the types of video files that can be played in the web browser.
     */
    public boolean isVideo() {
        String fileExtension = extensionOfSelectedFileInCaps();
        return (fileExtension.equals("MP4") || fileExtension.equals("OGG") || fileExtension.equals("WEBM"));
    }

    /*
    ========================================================
    Return Extension of the Selected File in Capital Letters
    ========================================================
     */
    public String extensionOfSelectedFileInCaps() {

        // Obtain the selected filename
        String userFileName = selected.getMealPhoto();

        // Extract the file extension from the filename
        String fileExtension = userFileName.substring(userFileName.lastIndexOf(".") + 1);

        // Return file extension in capital letters
        return fileExtension.toUpperCase();
    }

}
