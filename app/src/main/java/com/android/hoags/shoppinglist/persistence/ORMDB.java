package com.android.hoags.shoppinglist.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.hoags.shoppinglist.models.FlexProduct;
import com.android.hoags.shoppinglist.models.Product;
import com.android.hoags.shoppinglist.models.ShoppingList;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Oliver on 17-May-17.
 */

public class ORMDB extends OrmLiteSqliteOpenHelper {
    private static final String LOG = ORMDB.class.getName();
    private static final String DB_NAME = "shoppinglist.db";
    private static final int DB_VERSION = 1;

    public ORMDB(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            //create tables
            TableUtils.createTable(connectionSource, ShoppingList.class);
            TableUtils.createTable(connectionSource, Product.class);
            TableUtils.createTable(connectionSource, FlexProduct.class);
        } catch (SQLException e){
            Log.e(LOG, "Error creating database tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public Dao<ShoppingList, Integer> createShoppingListDAO(){
        try{
            return DaoManager.createDao(connectionSource, ShoppingList.class);
        } catch (SQLException ex){
            ex.printStackTrace();
            Log.e(LOG, "error creating DAO for ShoppingList class", ex);
        }
        return null;
    }

    public Dao<Product, Integer> createProductDAO(){
        try{
            return DaoManager.createDao(connectionSource, Product.class);
        } catch (SQLException ex){
            ex.printStackTrace();
            Log.e(LOG, "error creating DAO for Product class", ex);
        }
        return null;
    }

    public Dao<FlexProduct, Integer> createFlexProductDAO(){
        try{
            return DaoManager.createDao(connectionSource, FlexProduct.class);
        } catch (SQLException ex){
            ex.printStackTrace();
            Log.e(LOG, "error creating DAO for FlexProduct class", ex);
        }
        return null;
    }
}
