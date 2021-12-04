/*
 * Created by Ishaan Gulati on 2021.11.21
 * Copyright © 2021 Ishaan Gulati. All rights reserved.
 */

package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.GeoCoding;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class GeoCodingFacade extends  AbstractFacade<GeoCoding>{
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
    public GeoCodingFacade() {
        super(GeoCoding.class);
    }

    /*
    search by zipcode.
    * */
    public List<GeoCoding>  findByZipCode(String zipcode) {
        return getEntityManager().createQuery(
                        "SELECT g FROM GeoCoding g where  g.zipcode = :zipcode"
                ).setParameter("zipcode", zipcode)
                .getResultList();
    }

}
