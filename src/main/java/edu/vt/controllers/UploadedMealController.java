/*
 * Created by Team 5 on 2021.11.15
 * Copyright © 2021 Team 5. All rights reserved.
 */

package edu.vt.controllers;

import edu.vt.EntityBeans.SharedMeal;
import edu.vt.EntityBeans.UploadedMeal;
import edu.vt.EntityBeans.User;
import edu.vt.FacadeBeans.SharedMealFacade;
import edu.vt.FacadeBeans.UploadedMealFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("uploadedMealController")
@SessionScoped

public class UploadedMealController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    UploadedMealFacade bean into the instance variable 'uploadedMealFacade' after it is instantiated at runtime.
     */
    @EJB
    private UploadedMealFacade uploadedMealFacade;

    @EJB
    private SharedMealFacade sharedMealFacade;

    @Inject
    private SharedMealController sharedMealController;

    private SharedMeal sharedMealCopy;

    private String publicName;
    private UploadedMeal mealToShare;



    // 'selected' contains the object reference of the selected User File object
    private UploadedMeal selected;

    // 'listOfUploadedMeals' is a List containing the object references of User File objects
    private List<UploadedMeal> listOfUploadedMeals = null;

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
    public UploadedMeal getSelected() {
        return selected;
    }

    public void setSelected(UploadedMeal selected) {
        this.selected = selected;
    }

    public String getSelectedRowNumber() {
        return selectedRowNumber;
    }

    public void setSelectedRowNumber(String selectedRowNumber) {
        this.selectedRowNumber = selectedRowNumber;
    }

    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public UploadedMeal getMealToShare() {
        return mealToShare;
    }

    public void setMealToShare(UploadedMeal mealToShare) {
        this.mealToShare = mealToShare;
    }

    /*
            ***************************************************************
            Return the List of User Files that Belong to the Signed-In User
            ***************************************************************
             */
    public List<UploadedMeal> getListOfUploadedMeals() {

        if (listOfUploadedMeals == null) {
            /*
            'user', the object reference of the signed-in user, was put into the SessionMap
            in the initializeSessionMap() method in LoginManager upon user's sign in.
             */
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            User signedInUser = (User) sessionMap.get("user");

            // Obtain the database primary key of the signedInUser object
            Integer primaryKey = signedInUser.getId();

            // Obtain only those files from the database that belong to the signed-in user
            listOfUploadedMeals = uploadedMealFacade.findUserFilesByUserPrimaryKey(primaryKey);

            // Instantiate a new hash map object
            cleanedFileNameHashMap = new HashMap<>();

            /*
            cleanedFileNameHashMap<KEY, VALUE>
                KEY   = Integer fileId
                VALUE = String cleanedFileNameForSelected
             */
            listOfUploadedMeals.forEach(userFile -> {

                // Obtain the filename stored in CS5704-Team5-FileStorage/FileStorage as 'userId_filename'
                String storedFileName = userFile.getMealPhoto();

                // Remove the "userId_" (e.g., "4_") prefix in the stored filename
                String cleanedFileName = storedFileName.substring(storedFileName.indexOf("_") + 1);

                // Obtain the file database Primary Key id
                Integer fileId = userFile.getId();

                // Create an entry in the hash map as a key-value pair
                cleanedFileNameHashMap.put(fileId, cleanedFileName);
            });
        }
        return listOfUploadedMeals;
    }

    /*
    ================
    Instance Methods
    ================
     */

    // The constants CREATE, DELETE and UPDATE are defined in JsfUtil.java

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
        we can reset listOfUploadedMeals to null so that it will be recreated with the newly created user file.
         */
        if (!JsfUtil.isValidationFailed()) {
            selected = null;            // Remove selection
            listOfUploadedMeals = null;     // Invalidate listOfUploadedMeals to trigger re-query.
        }
    }

    public void prepareShare(UploadedMeal meal){
        mealToShare = meal;
    }
    public void share() {
        Methods.preserveMessages();

        persistShare(mealToShare, publicName,"Meal was Successfully Shared!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The CREATE operation is successfully performed.
            selected = null;        // Remove selection
            listOfUploadedMeals = null;    // Invalidate listOfUploadedMeals to trigger re-query.
        }
    }

    public void cancel(){
        publicName = null;
        mealToShare = null;
    }


    // We do not allow update of a user file.
    public void update() {
        persist(PersistAction.UPDATE,"User File was Successfully Updated!");
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
                    
                     UploadedMealFacade inherits the edit(selected) method from the AbstractFacade class.
                     */
                    uploadedMealFacade.edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove method performs the DELETE operation in the database.
                    
                     UploadedMealFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    uploadedMealFacade.remove(selected);
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

    private void persistShare(UploadedMeal mealToShare, String publicName, String successMessage) {


        try {
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            User signedInUser = (User) sessionMap.get("user");

            sharedMealCopy = new SharedMeal(mealToShare.getMealPhoto(), mealToShare.getMealName(), mealToShare.getMealDescription(), signedInUser, publicName);

            publicName = null;
            mealToShare = null;

            sharedMealFacade.edit(sharedMealCopy);

            sharedMealController.refreshFileList();

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



    public UploadedMeal getUserFile(Integer id) {
        return uploadedMealFacade.find(id);
    }

    @FacesConverter(forClass = UploadedMeal.class)
    public static class UserFileControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UploadedMealController controller = (UploadedMealController) facesContext.getApplication().getELResolver().
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
            if (object instanceof UploadedMeal) {
                UploadedMeal o = (UploadedMeal) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                        "object {0} is of type {1}; expected type: {2}",
                        new Object[]{object, object.getClass().getName(), UploadedMeal.class.getName()});
                return null;
            }
        }
    }

    public String deleteUserFile(UploadedMeal uploadedMealToDelete) {

        /*
        We need to preserve the messages since we will redirect to show a
        different JSF page after successful deletion of the user file.
         */
        Methods.preserveMessages();

        if (uploadedMealToDelete == null) {
            Methods.showMessage("Fatal Error", "No File Selected!", "You do not have a file to delete!");
            return "";
        } else {
            try {
                // Delete the file from CS5704-Team5-FileStorage/FileStorage
                Files.deleteIfExists(Paths.get(uploadedMealToDelete.getFilePath()));

                // Delete the user file record from the database
                uploadedMealFacade.remove(uploadedMealToDelete);
                // UploadedMealFacade inherits the remove() method from AbstractFacade

                Methods.showMessage("Information", "Success!", "Selected File is Successfully Deleted!");

                // See method below
                refreshFileList();

                return "/userFile/FavoriteMeals?faces-redirect=true";

            } catch (IOException ex) {
                Methods.showMessage("Fatal Error", "Something went wrong while deleting the user file!",
                        "See: " + ex.getMessage());
                return "";
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
        By setting the listOfUploadedMeals to null, we force the getListOfUserFiles
        method above to retrieve all of the user's files again.
         */
        selected = null;            // Remove selection
        listOfUploadedMeals = null;     // Invalidate listOfUploadedMeals to trigger re-query.
    }

    /*
    =======================================
    Return Image or Icon based on File Type
    =======================================
    fileId is the database primary key value for a user file
    Return image if it is an image file; otherwise, return file type icon

    Any type of file can be uploaded or downloaded.
    We identify the types of files that can be displayed or played in the web browser.
    */
    public String fileTypeIcon(Integer fileId) {

        // Obtain the object reference of the UploadedMeal whose primary key = fileId
        UploadedMeal uploadedMeal = uploadedMealFacade.getUserFile(fileId);

        // Obtain the uploadedMeal's filename as it is stored in the GoodBiteDB database
        String imageFileName = uploadedMeal.getMealPhoto();

        // Extract the file extension from the filename
        String fileExtension = imageFileName.substring(imageFileName.lastIndexOf(".") + 1);

        // Convert file extension to uppercase
        String fileExtensionInCaps = fileExtension.toUpperCase();

        switch (fileExtensionInCaps) {
            case "JPG":
            case "JPEG":
            case "PNG":
            case "GIF":
                // If it is an image file, return the image file URI
                return Constants.FILES_URI + imageFileName;
            case "MP4":
            case "OGG":
            case "WEBM":
                // If it is a video file, return the videoFile icon
                return "/resources/images/videoFile.png";
            default:
                // If it is another file type, return the viewFile icon
                return "/resources/images/viewFile.png";
        }
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

    public String getFileURI(UploadedMeal file){
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
