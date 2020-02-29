package com.android.hoags.shoppinglist.helpers;

import com.android.hoags.shoppinglist.models.FlexProduct;
import com.android.hoags.shoppinglist.models.Product;

/**
 * Created by Oliver on 29-May-17.
 */

public interface OnProductListItemListener {
    void productItemWasDragged(Product product);
    void productItemRemove(FlexProduct flexProduct, int position);
    void productItemPurchase(FlexProduct flexProduct, int position, boolean isBuy);
}
