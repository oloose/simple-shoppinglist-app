package com.android.hoags.shoppinglist.models;

import android.graphics.Color;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Random;


/**
 * Created by Oliver on 17-May-17.
 */
@DatabaseTable(tableName = "products")
public class Product {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name = "";
    @DatabaseField
    private double price;
    @DatabaseField
    private String imageURI;
    @DatabaseField
    private int color;
    @DatabaseField
    private String seller = "";
    @DatabaseField
    private String description = "";

    public Product(){}

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getName() {
        if(name != null){
            return name;
        } else {
            return "";
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Generates a random ARGB color and sets the result as a hex-string as the products color.
     */
    public void setColor(){
        Random rand = new Random();
        this.color = Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public String getDescription() {
        if(description != null){
            return  description;
        } else {
            return "";
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeller() {
        if(seller != null){
            return seller;
        } else {
            return "";
        }
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void updateProduct(Product editedProduct) {
        setName(editedProduct.getName());
        setSeller(editedProduct.getSeller());
        setPrice(editedProduct.getPrice());
        setDescription(editedProduct.getDescription());
    }
}
