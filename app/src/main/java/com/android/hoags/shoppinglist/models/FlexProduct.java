package com.android.hoags.shoppinglist.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Oliver on 17-May-17.
 */
@DatabaseTable(tableName = "flex_products")
public class FlexProduct extends Product {
    @DatabaseField
    private boolean isPurchased = false;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private ShoppingList shoppingList;
    @DatabaseField(foreign = true, foreignAutoCreate = true)
    private Product product;

    public FlexProduct(){}

    public FlexProduct(Product product, ShoppingList shoppingList){
        this.setProduct(product);
        this.setShoppingList(shoppingList);

        this.setName(product.getName());
        this.setPrice(product.getPrice());
        this.setColor(product.getColor());
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
