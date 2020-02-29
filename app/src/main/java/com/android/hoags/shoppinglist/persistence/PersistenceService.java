package com.android.hoags.shoppinglist.persistence;

import android.content.Context;
import android.util.Log;

import com.android.hoags.shoppinglist.models.FlexProduct;
import com.android.hoags.shoppinglist.models.Product;
import com.android.hoags.shoppinglist.models.ShoppingList;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Oliver on 17-May-17.
 */

public class PersistenceService implements IPersistence {
    private static final String LOG = PersistenceService.class.getName();
    private ORMDB db;
    private Dao<ShoppingList, Integer> shoppingListsDao;
    private Dao<Product, Integer> productDao;
    private Dao<FlexProduct, Integer> flexProductDao;

    public PersistenceService(Context context){
        db = new ORMDB(context);
        shoppingListsDao = db.createShoppingListDAO();
        productDao = db.createProductDAO();
        flexProductDao = db.createFlexProductDAO();
    }

    //region ShoppingList
    @Override
    public void saveShoppingList(ShoppingList shoppingList) {
        try {
            shoppingListsDao.createOrUpdate(shoppingList);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while saving ShoppingLists");
        }
    }

    @Override
    public void updateShoppingList(ShoppingList shoppingList) {
        try {
            shoppingListsDao.createOrUpdate(shoppingList);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while updating ShoppingLists");
        }
    }

    @Override
    public void deleteShoppingList(ShoppingList shoppingList) {
        try {
            deleteAllFlexProductsByShoppingList(shoppingList);
            shoppingListsDao.deleteById(shoppingList.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while deleting ShoppingLists");
        }
    }

    @Override
    public void deleteAllShoppingList() {
        try {
            shoppingListsDao.delete(shoppingListsDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while deleting all ShoppingLists");
        }
    }

    @Override
    public ShoppingList getShoppingListById(int id) {
        try {
            return shoppingListsDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while getting ShoppingLists");
        }
        return null;
    }

    @Override
    public Collection<ShoppingList> getAllShoppingLists() {
        try {
            return shoppingListsDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while getting all ShoppingLists");
        }
        return null;
    }

    @Override
    public Collection<ShoppingList> getAllShoppingListsFavorites() {
        ArrayList<ShoppingList> result = new ArrayList<>();

        //query for all shoppinglists where 'isFavorite' is true
        QueryBuilder<ShoppingList, Integer> queryBuilder = shoppingListsDao.queryBuilder();
        Where<ShoppingList, Integer> where = queryBuilder.where();
        try {
            where.eq("isFavorite", true);
            result = new ArrayList<>(where.query());
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while getting all favorite ShoppingLists");
        }

        return result;
    }
    //endregion

    //region Product
    @Override
    public void saveProduct(Product product) {
        try {
            productDao.createOrUpdate(product);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while saving ShoppingLists");
        }
    }

    @Override
    public Product getProductByName(String productName) {
        QueryBuilder<Product, Integer> queryBuilder = productDao.queryBuilder();
        Where<Product, Integer> where = queryBuilder.where();
        try{
            where.eq("name", productName);
            return where.query().get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while getting product by name");
        }
        return null;
    }


    @Override
    public void deleteProduct(Product product) {
        try {
            deleteAllFlexProductsByProduct(product);
            productDao.delete(product);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while deleting product");
        }
    }

    public void deleteAllFlexProductsByProduct(Product product) {
        DeleteBuilder<FlexProduct, Integer> deleteBuilder = flexProductDao.deleteBuilder();
        Where<FlexProduct, Integer> where = deleteBuilder.where();
        try{
            where.eq("product_id", product.getId());
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while getting deleting all flex products by product");
        }
    }

    @Override
    public void updateProduct(Product product) {
        try {
            productDao.update(product);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while updating product");
        }
    }

    @Override
    public Product getProductById(int id) {
        try {
            return productDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while getting product");
        }
        return null;
    }

    @Override
    public Collection<Product> getAllProducts() {
        try {
            return productDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while getting all Products");
        }
        return null;
    }

    @Override
    public Collection<Product> getAllProductsByName(String name) {
        return null;
    }
    //endregion

    //region FlexProduct
    @Override
    public void saveFlexProduct(FlexProduct flexProduct) {
        try {
            flexProductDao.createOrUpdate(flexProduct);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while saving FlexProduct");
        }
    }

    @Override
    public void deleteFlexProduct(FlexProduct flexProduct) {
        try {
            flexProductDao.delete(flexProduct);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while deleting FlexProduct");
        }
    }

    @Override
    public void deleteAllFlexProductsByShoppingList(ShoppingList shoppingList) {
        DeleteBuilder<FlexProduct, Integer> deleteBuilder = flexProductDao.deleteBuilder();
        Where<FlexProduct, Integer> where = deleteBuilder.where();
        try{
            where.eq("shoppingList_id", shoppingList.getId());
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while getting deleting all flex products by shopping list");
        }
    }

    @Override
    public void updateFlexProduct(FlexProduct flexProduct) {
        try {
            flexProductDao.update(flexProduct);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while saving updated flex product");
        }
    }

    @Override
    public void updateAllFlexProductsByProduct(Product product) {
        List<FlexProduct> flexProductList = new ArrayList<>(getAllFlexProductsByProduct(product));
        for(FlexProduct flexProduct : flexProductList){
            flexProduct.updateProduct(product);
            updateFlexProduct(flexProduct);
        }
    }

    @Override
    public FlexProduct getFlexProductById(int id) {
        return null;
    }

    @Override
    public Collection<FlexProduct> getAllFlexProductsByProduct(Product product) {
        QueryBuilder<FlexProduct, Integer> queryBuilder = flexProductDao.queryBuilder();
        Where<FlexProduct, Integer> where = queryBuilder.where();
        try{
            where.eq("product_id", product.getId());
            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while updating flex products by product");
        }
        return null;
    }

    @Override
    public Collection<FlexProduct> getAllFlexProductsByShoppingList(ShoppingList shoppingList) {
        QueryBuilder<FlexProduct, Integer> queryBuilder = flexProductDao.queryBuilder();
        Where<FlexProduct, Integer> where = queryBuilder.where();
        try{
            return where.eq("shoppingList_id", shoppingList.getId()).query();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while getting all flex products by shoppinglist");
        }
        return null;
    }

    @Override
    public Collection<FlexProduct> getAllFlexProducts() {
        try {
            return flexProductDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while getting all FlexProduct");
        }
        return null;
    }

    @Override
    public boolean existsFlexProductWithProduct(Product product) {
        QueryBuilder<FlexProduct, Integer> queryBuilder = flexProductDao.queryBuilder();
        Where<FlexProduct, Integer> where = queryBuilder.where();
        try{
            if(where.eq("product_id", product.getId()).query().isEmpty()){
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG, "error while query for all flex products by product");
        }
        return false;
    }
    //endregion
}
