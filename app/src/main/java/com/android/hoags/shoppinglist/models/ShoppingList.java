package com.android.hoags.shoppinglist.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.LazyForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Oliver on 17-May-17.
 */
@DatabaseTable(tableName = "shpping_lists")
public class ShoppingList {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private boolean isFavorite = false;
    @DatabaseField
    private String title;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<FlexProduct> productList;
    @DatabaseField
    private Date lastUsed;

    public ShoppingList(){
        setLastUsed();
    }

    public ShoppingList(String title){
        this.title = title;
        lastUsed = Calendar.getInstance().getTime();
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ForeignCollection<FlexProduct> getProductList() {
        return productList;
    }

    public void setProductList(ForeignCollection<FlexProduct> productList) {
        this.productList = productList;
    }

    public double getPrice(){
        double price = 0;
        if(productList != null){
            for (Product product: productList) {
                price += product.getPrice();
            }
        }
        return price;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    /**
     * Sets now as the last time this shopping list was used
     */
    public void setLastUsed(){
        lastUsed = Calendar.getInstance().getTime();
    }

    public void addFlexProduct(FlexProduct flexProduct) {
        productList.add(flexProduct);
    }
}
