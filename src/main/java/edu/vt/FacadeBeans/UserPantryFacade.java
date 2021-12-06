

/*
 * Created by Neha Surana on 2021.11.1
 * Copyright © 2021 Neha Surana. All rights reserved.
 */

package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.UserPantry;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class UserPantryFacade extends AbstractFacade<UserPantry> {
    /*
    ---------------------------------------------------------------------------------------------
    The EntityManager is an API that enables database CRUD (Create Read Update Delete) operations
    and complex database searches. An EntityManager instance is created to manage entities
    that are defined by a persistence unit. The @PersistenceContext annotation below associates
    the entityManager instance with the persistence unitName identified below.
    ---------------------------------------------------------------------------------------------
     */
    @PersistenceContext(unitName = "GoodBitePU")
    private EntityManager entityManager;

    // Obtain the object reference of the EntityManager instance in charge of
    // managing the entities in the persistence context identified above.
    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /*
    This constructor method invokes its parent AbstractFacade's constructor method,
    which in turn initializes its entity class type T and entityClass instance variable.
     */
    public UserPantryFacade() {
        super(UserPantry.class);
    }


    /*
     *********************
     *   Other Methods   *
     *********************
     */

    // Returns the object reference of the Recipe object whose name is name
    public UserPantry findByName(String ingredient) {
        /*
        The following @NamedQuery definition is given in Recipe.java entity class file:
        @NamedQuery(name = "Recipe.findByName", query = "SELECT c FROM Recipe c WHERE c.name = :name")
         */

        if (entityManager.createNamedQuery("UserPantry.findByIngredientName")
                .setParameter("ingredient", ingredient)
                .getResultList().isEmpty()) {

            // Return null if no recipe object exists
            return null;

        } else {

            // Return the Object reference of the recipe object whose name is name
            return (UserPantry) (entityManager.createNamedQuery("UserPantry.findByIngredientName")
                    .setParameter("ingredient", ingredient)
                    .getSingleResult());
        }
    }

    //Query Methods to search the Database

    /*
    -----------------------------
    Search Category: Name
    -----------------------------
     */
    //Searches RecipesDB for recipes where recipes name contains the searchString entered by the user.
    public List<UserPantry> nameQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the Car name
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT c FROM UserPantry c WHERE c.ingredient LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    //    public List<UserPantry> findUserPantryByUserId
    public List<UserPantry> findUserPantryByUserId(Integer userId) {
        /*
        The following @NamedQuery definition is given in UserVideo entity class file:
            @NamedQuery(name = "UserVideo.findVideosByUserDatabasePrimaryKey", query = "SELECT p FROM UserPantry p WHERE p.userId.id = :primaryKey")

        userId.id --> User object's database primary key
        The following statement obtains the results from the named database query.
         */

        return (List<UserPantry>) entityManager.createNamedQuery("UserPantry.findUserPantryByUserId")
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<UserPantry> findAvailableItems(Integer userId) {
        /*
        The following @NamedQuery definition is given in UserVideo entity class file:
            @NamedQuery(name = "UserVideo.findVideosByUserDatabasePrimaryKey", query = "SELECT p FROM UserPantry p WHERE p.userId.id = :primaryKey")

        userId.id --> User object's database primary key
        The following statement obtains the results from the named database query.
         */

        return (List<UserPantry>) entityManager.createNamedQuery("UserPantry.findAvailableItems")
                .setParameter("userId", userId)
                .getResultList();
    }


}


