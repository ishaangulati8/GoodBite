/*
 * Created by Osman Balci on 2021.11.11
 * Copyright © 2021 Osman Balci. All rights reserved.
 */

package edu.vt.galleria;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@Named(value = "photoService")
@ApplicationScoped
public class PhotoService {
    /*
    ============================
    Instance Variable (Property)
    ============================
     */
    private List<Photo> listOfPhotos;

    /*
    The PostConstruct annotation is used on a method that needs to be executed after
    dependency injection is done to perform any initialization. The initialization
    method init() is the first method invoked before this class is put into service.
     */
    @PostConstruct
    public void init() {
        listOfPhotos = new ArrayList<>();

        listOfPhotos.add(new Photo("/resources/images/slider/photo1.png", "/resources/images/slider/photos1.png",
                "Description for Photo 1", "Health and Fitness are Key to a Happy Life"));
        listOfPhotos.add(new Photo("/resources/images/slider/photo2.png", "/resources/images/slider/photos2.png",
                "Description for Photo 2", "Apple Fitness+ is the first Fitness Experience built around Apple Watch"));
        listOfPhotos.add(new Photo("/resources/images/slider/photo5.png", "/resources/images/slider/photos5.png",
                "Description for Photo 5", "Body Mass Index for Women"));
        listOfPhotos.add(new Photo("/resources/images/slider/photo4.png", "/resources/images/slider/photos4.png",
                "Description for Photo 3", "Image 4"));


    }

    /*
    =============
    Getter Method
    =============
     */
    public List<Photo> getListOfPhotos() {
        return listOfPhotos;
    }
}
