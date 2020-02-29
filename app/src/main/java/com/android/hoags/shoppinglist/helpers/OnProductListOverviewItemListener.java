package com.android.hoags.shoppinglist.helpers;

import com.android.hoags.shoppinglist.models.Product;

/**
 * Created by Oliver on 13-Jun-17.
 */

public interface OnProductListOverviewItemListener {
    void onItemRemoved(Product product, int position);
    void onItemEdit(Product product, int position);
    void onItemEdited(int productId, int position);
    void onItemAdded(String productName);
}
