/*
 * Created by Ishaan Gulati on 2021.12.4
 * Copyright © 2021 Ishaan Gulati. All rights reserved.
 */

package edu.vt.EntityBeans;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


/*
The @Entity annotation designates this class as a JPA Entity POJO class
representing the User table in the CloudDriveDB database.
 */
@Entity

// Name of the database table represented
@Table(name = "UserPantry")
@NamedQueries({
    /*
    private User userId;    --> userId is the object reference of the User object.
    userId.id               --> User object's database primary key
     */
        @NamedQuery(name = "UserPantry.findAll", query = "SELECT u FROM UserPantry u")
        , @NamedQuery(name = "UserPantry.findById", query = "SELECT u FROM UserPantry u WHERE u.id = :id")
        , @NamedQuery(name = "UserPantry.findByIngredientName", query = "SELECT u FROM UserPantry u WHERE u.ingredient = :ingredient")
        , @NamedQuery(name = "UserPantry.findAvailableItems", query = "SELECT u FROM UserPantry u WHERE u.userId.id = :userId and u.quantity > 0")
        , @NamedQuery(name = "UserPantry.findUserPantryByUserId", query = "SELECT u FROM UserPantry u WHERE u.userId.id = :userId")
})

public class UserPantry implements Serializable {
    /**
     * CREATE TABLE `UserPantry` (
     * `id` int unsigned NOT NULL AUTO_INCREMENT,
     * PRIMARY KEY (`id`),
     * `ingredient` varchar(256) NOT NULL,
     * `calories` decimal(8, 2) DEFAULT NULL,
     * `weight` decimal(8, 2) DEFAULT NULL,
     * `user_id` int unsigned DEFAULT NULL,
     * FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
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
    @Column(name = "ingredient")
    private String ingredient;

//    @Column(precision = 5, scale = 4)
//    @Type(type = "big_decimal")
//    private double similarity;

    @Basic(optional = false)
    @NotNull
//    @Size(min = 1, max = 256)
    @Column(name = "calories", precision = 8, scale = 2)
    private Double calories;


    @Basic(optional = false)
    @NotNull
//    @Size(min = 1, max = 256)
    @Column(name = "quantity", precision = 8, scale = 2)
    private Double quantity;

    // user_id INT UNSIGNED
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User userId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "unit")
    private String unit;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2048)
    @Column(name = "nutrients")
    private String nutrients;


    public UserPantry() {
    }

    public UserPantry(Integer id, String ingredient, Double calories, Double quantity, User userId, String unit, String nutrients) {
        this.id = id;
        this.ingredient = ingredient;
        this.calories = calories;
        this.quantity = quantity;
        this.userId = userId;
        this.unit = unit;
        this.nutrients = nutrients;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNutrients() {
        return nutrients;
    }

    public void setNutrients(String nutrients) {
        this.nutrients = nutrients;
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
     Checks if the UserFile object identified by 'object' is the same as the UserFile object identified by 'id'
     Parameter object = UserFile object identified by 'object'
     Returns True if the UserFile 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserPantry)) {
            return false;
        }
        UserPantry other = (UserPantry) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return ingredient.toString();
    }

}
