/*
 * Created by Team 5 on 2021.12.4
 * Copyright © 2021 Team 5. All rights reserved.
 */

package edu.vt.EntityBeans;


import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;


/*
The @Entity annotation designates this class as a JPA Entity POJO class
representing the User table in the GoodBiteDB database.
 */
@Entity

// Name of the database table represented
@Table(name = "UserRecipe")
@NamedQueries({
    /*
    private User userId;    --> userId is the object reference of the User object.
    userId.id               --> User object's database primary key
     */
        @NamedQuery(name = "UserRecipe.findAll", query = "SELECT u FROM UserRecipe u")
        , @NamedQuery(name = "UserRecipe.findById", query = "SELECT u FROM UserRecipe u WHERE u.id = :id")
        , @NamedQuery(name = "UserRecipe.findByName", query = "SELECT u FROM UserRecipe u WHERE u.name = :name")
        , @NamedQuery(name = "UserRecipe.findUserRecipeByUserId", query = "SELECT u FROM UserRecipe u WHERE u.userId.id = :userId")
})

public class UserRecipe implements Serializable{
    /**
     * CREATE TABLE `UserRecipe` (
     *                               `id` int unsigned NOT NULL AUTO_INCREMENT,
     *                               `name` varchar(256) NOT NULL,
     *                               `ingredients` varchar(2048) NOT NULL,
     *                               `nutrients` varchar(2048) DEFAULT NULL,
     *                               `image_url` varchar(512) DEFAULT NULL,
     *                               `description` varchar(1024) DEFAULT NULL,
     *                               `category` varchar(128) DEFAULT NULL,
     *                               `cuisine` varchar(128) DEFAULT NULL,
     *                               `user_id` int unsigned DEFAULT NULL,
     *                               `source_url` varchar(1024) DEFAULT NULL,
     *                               `health_labels` varchar(2048) DEFAULT NULL,
     *                               `diet_labels` varchar(2048) DEFAULT NULL,
     *                               `cautions` varchar(1024) DEFAULT NULL,
     *                               `url` varchar(512) DEFAULT NULL,
     *                               PRIMARY KEY (`id`),
     *                               UNIQUE KEY `category_UNIQUE` (`category`),
     *                               KEY `user_id` (`user_id`),
     *                               CONSTRAINT `userrecipe_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`) ON DELETE CASCADE
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
     */

    private static final long serialVersionUID = 1L;

    // id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    // name VARCHAR(256) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "name")
    private String name;

    //  ingredients(2048) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2048)
    @Column(name = "ingredients")
    private String ingredients;

    //  nutrients(2048) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2048)
    @Column(name = "nutrients")
    private String nutrients;

    //  image_url(512) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 512)
    @Column(name = "image_url")
    private String imageUrl;

    //  description(1024) NOT NULL
    @Basic(optional = false)
    @Size(min = 0, max = 1024)
    @Column(name = "description", nullable = true)
    private String description;

    //  description(128) NOT NULL
    @Basic(optional = false)
    @Size(min = 0, max = 128)
    @Column(name = "category", nullable = true)
    private String category;

    //  cuisine(128) NOT NULL
    @Basic(optional = false)
    @Size(min = 0, max = 128)
    @Column(name = "cuisine", nullable = true)
    private String cuisine;

    //  source_url(1024) NOT NULL
    @Basic(optional = false)
    @Size(min = 0, max = 1024)
    @Column(name = "source_url", nullable = true)
    private String sourceUrl;


    //  health_labels(2048) NOT NULL
    @Basic(optional = false)
    @Size(min = 0, max = 2048)
    @Column(name = "health_labels", nullable = true)
    private String healthLabels;

    //  diet_labels(2048) NOT NULL
    @Basic(optional = false)
    @Size(min = 0, max = 2048)
    @Column(name = "diet_labels", nullable = true)
    private String dietLabels;

    //  cautions(1024`) NOT NULL
    @Basic(optional = false)
    @Size(min = 0, max = 1024)
    @Column(name = "cautions", nullable = true)
    private String cautions;

    //  cautions(512) NOT NULL
    @Basic(optional = false)
    @Size(min = 0, max = 512)
    @Column(name = "url", nullable = true)
    private String url;

    //  source(256)
    @Basic(optional = false)
    @Size(min = 0, max = 256)
    @Column(name = "source", nullable = true)
    private String source;

    // user_id INT UNSIGNED
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User userId;

    public UserRecipe() {}

    public UserRecipe(String name, String ingredients, String nutrients, String imageUrl, String description, String category, String cuisine, String sourceUrl, String healthLabels, String dietLabels, String cautions, String url, User userId) {
        this.name = name;
        this.ingredients = ingredients;
        this.nutrients = nutrients;
        this.imageUrl = imageUrl;
        this.description = description;
        this.category = category;
        this.cuisine = cuisine;
        this.sourceUrl = sourceUrl;
        this.healthLabels = healthLabels;
        this.dietLabels = dietLabels;
        this.cautions = cautions;
        this.url = url;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getNutrients() {
        return nutrients;
    }

    public void setNutrients(String nutrients) {
        this.nutrients = nutrients;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getHealthLabels() {
        return healthLabels;
    }

    public void setHealthLabels(String healthLabels) {
        this.healthLabels = healthLabels;
    }

    public String getDietLabels() {
        return dietLabels;
    }

    public void setDietLabels(String dietLabels) {
        this.dietLabels = dietLabels;
    }

    public String getCautions() {
        return cautions;
    }

    public void setCautions(String cautions) {
        this.cautions = cautions;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    /*
    ================================
    Instance Methods Used Internally
    ================================
     */

    // Generate and return a hash code value for the object with database primary key id
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /*
     Checks if the UserRecipe object identified by 'object' is the same as the UserPhoto object identified by 'id'
     Parameter object = UserRecipe object identified by 'object'
     Returns True if the UserRecipe 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserRecipe)) {
            return false;
        }
        UserRecipe other = (UserRecipe) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return id.toString();
    }

}
