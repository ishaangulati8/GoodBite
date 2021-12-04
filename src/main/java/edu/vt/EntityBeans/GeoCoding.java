/*
 * Created by Ishaan Gulati on 2021.11.21
 * Copyright © 2021 Ishaan Gulati. All rights reserved.
 */

package edu.vt.EntityBeans;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/*
        The @Entity annotation designates this class as a JPA Entity POJO class
representing the Movie table in the MoviesDB database.
        */
@Entity

// Name of the database table represented
@Table(name = "GeoCoding")


public class GeoCoding {
    /*
    CREATE TABLE `GeoCoding` (
      `id` int unsigned NOT NULL AUTO_INCREMENT,
      `zipcode` varchar(10) NOT NULL,
      `lat` decimal(11,6) NOT NULL,
      `lng` decimal(11,6) NOT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `zipcode_UNIQUE` (`zipcode`)
    ) ENGINE=InnoDB AUTO_INCREMENT=33145 DEFAULT CHARSET=utf8mb3;
    * */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    //zipcode
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "zipcode")
    private String zipcode;

    // lat
    @Basic(optional = false)
    @NotNull
    @Column(name = "lat")
    private Double lat;

    // lat
    @Basic(optional = false)
    @NotNull
    @Column(name = "lng")
    private Double lng;

    public GeoCoding() {}

    public GeoCoding(Integer id, String zipcode, Double lat, Double lng) {
        this.id = id;
        this.zipcode = zipcode;
        this.lat = lat;
        this.lng = lng;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
