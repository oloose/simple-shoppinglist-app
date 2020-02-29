package com.android.hoags.shoppinglist.persistence;

import com.android.hoags.shoppinglist.models.FlexProduct;
import com.android.hoags.shoppinglist.models.Product;
import com.android.hoags.shoppinglist.models.ShoppingList;

import java.util.Collection;
import java.util.List;

/**
 * Created by Oliver on 17-May-17.
 */

public interface IPersistence {
    /*#######################
     *# ProductShoppingList #
     *#######################*/
    /**
     * Saves a ShoppingList to the database.
     * @param shoppingList ShoppingList to save.
     */
    void saveShoppingList(ShoppingList shoppingList);
    /**
     * Updates the database entry of the given ShoppingList.
     * @param shoppingList ShoppingList to update.
     */
    void updateShoppingList(ShoppingList shoppingList);
    /**
     * Deletes the given ShoppingList from the database.
     * @param shoppingList ShoppingList to delete.
     */
    void deleteShoppingList(ShoppingList shoppingList);

    /**
     * Deletes all ShoppingLists from the database.
     */
    void deleteAllShoppingList();
    /**
     * Returns the ShoppingList with the given id.
     * @param id Id of the Shopping list to query for.
     * @return ShoppingList if an Element was found, else null
     */
    ShoppingList getShoppingListById(int id);
    /**
     * Returns a collection of all ShoppingLists in the database.
     * @return ShoppingList collection containing the query results.
     */
    Collection<ShoppingList> getAllShoppingLists();
    /**
     * Returns a collection of all ShoppingLists in the database, which are marked as 'Favorite'
     * @return ShoppingList collection containing the query results.
     */
    Collection<ShoppingList> getAllShoppingListsFavorites();

    /*###########
     *# Product #
     *###########*/
    /**
     * Saves a Product to the database.
     * @param product Product to save.
     */
    void saveProduct(Product product);
    /**
     * Gets a product by its name.
     */
    Product getProductByName(String productName);
    /**
     * Updates the database entry of the given Product.
     * @param product Product to update.
     */
    void deleteProduct(Product product);
    /**
     * Updates the database entry of the given Product.
     * @param product Product to update.
     */
    void updateProduct(Product product);
    /**
     * Returns the Product with the given id.
     * @param id Id of the Product to query for.
     * @return A Product if an Element was found, else null
     */
    Product getProductById(int id);
    /**
     * Returns a collection of all Products in the database.
     * @return Product collection containing the query results.
     */
    Collection<Product> getAllProducts();
    /**
     * Returns a collection of all Products in the database containing the given string in Product.name.
     *
     * @param name Name to query by.
     * @return Product collection containing the query results
     */
    Collection<Product> getAllProductsByName(String name);

    /*###############
     *# FlexProduct #
     *###############*/
    /**
     * Saves a FlexProduct to the database.
     * @param flexProduct FlexProduct to save.
     */
    void saveFlexProduct(FlexProduct flexProduct);
    /**
     * Deletes the given FlexProduct from the database.
     * @param flexProduct Product to delete.
     */
    void deleteFlexProduct(FlexProduct flexProduct);

    /**
     * Deletes all FlexProducts used in the given shopping list.
     *
     * @param shoppingList ShoppingList containing FlexProducts.
     */
    void deleteAllFlexProductsByShoppingList(ShoppingList shoppingList);
    /**
     * Updates the database entry of the given FlexProduct.
     * @param flexProduct FlexProduct to update.
     */
    void updateFlexProduct(FlexProduct flexProduct);
    /**
     * Updates all database entries originating from the given product.
     * @param product Product to update FlexProducts from.
     */
    void updateAllFlexProductsByProduct(Product product);
    /**
     * Returns the FlexProduct with the given id.
     * @param id Id of the FlexProduct to query for.
     * @return A FlexProduct if an Element was found, else null
     */
    FlexProduct getFlexProductById(int id);
    /**
     * Returns a collection of all FlexProducts in the database containing the given Product.
     *
     * @param product Product to query by.
     * @return FlexProduct collection containing the query results
     */
    Collection<FlexProduct> getAllFlexProductsByProduct(Product product);
    /**
     * Returns a collection of all FlexProducts in the database containing the given ShoppingList.
     *
     * @param shoppingList ShoppingList to query by.
     * @return FlexProduct collection containing the query results
     */
    Collection<FlexProduct> getAllFlexProductsByShoppingList(ShoppingList shoppingList);
    /**
     * Returns a collection of all FlexProducts in the database.
     *
     * @return FlexProduct collection containing the query results
     */
    Collection<FlexProduct> getAllFlexProducts();
    /**
     * Checks if FlexProducts with the given underlying product exist, and returns true.
     *
     * @param product The underlying product.
     * @return true if at least one flex product exists.
     */
    boolean existsFlexProductWithProduct(Product product);
    /**
     * Removes all FlexProducts with the given underlying product from the database.
     *
     * @param product The underlying product.
     */
    void deleteAllFlexProductsByProduct(Product product);
}
