package com.android.hoags.shoppinglist.helpers;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import com.android.hoags.shoppinglist.models.Product;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Oliver on 20-May-17.
 */

public final class StaticHelper {
    //use Local.US for "." separation
    public static final DecimalFormat decimalFormat = new DecimalFormat(
            "#0.00", DecimalFormatSymbols.getInstance(Locale.US));

    public static Drawable generateDrawable(Product product, int size){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(product.getColor());
        size = (int) Math.round(size * 0.60);
        drawable.setSize(size,size);

        return drawable;
    }

    public static void snackbar(String message, CoordinatorLayout coordinatorLayout){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /**
     * Sorts all products in productList by name
     */
    public static void sortProductList(List<Product> productList){
        //Sort the productList by name of products (A->Z)
        Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
    }

}
