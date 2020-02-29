package com.android.hoags.shoppinglist.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hoags.shoppinglist.R;
import com.android.hoags.shoppinglist.activities.ProductDialogActivity;
import com.android.hoags.shoppinglist.helpers.DragListener;
import com.android.hoags.shoppinglist.helpers.OnProductListItemListener;
import com.android.hoags.shoppinglist.helpers.StaticHelper;
import com.android.hoags.shoppinglist.models.FlexProduct;

import java.util.List;

import static com.android.hoags.shoppinglist.helpers.StaticHelper.decimalFormat;

/**
 * Created by Oliver on 11-June-17.
 */

public class FlexProductListRecyclerViewAdapter extends RecyclerView.Adapter<FlexProductListRecyclerViewAdapter.ViewHolder> {
    private List<FlexProduct> flexProductList;
    private OnProductListItemListener listener;

    public FlexProductListRecyclerViewAdapter(List<FlexProduct> flexProducts,
                                              OnProductListItemListener listener){
        this.flexProductList = flexProducts;
        this.listener = listener;
    }


    @Override
    public FlexProductListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_flex_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FlexProductListRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.flexProduct = flexProductList.get(position);

        if(holder.flexProduct.isPurchased()){
            holder.ivCart.setVisibility(View.VISIBLE);
            holder.tvPrice.setVisibility(View.VISIBLE);
            holder.tvPrice.setText(decimalFormat.format(holder.flexProduct.getPrice()));
        } else{
            holder.ivCart.setVisibility(View.GONE);
            holder.tvPrice.setVisibility(View.GONE);
        }

        holder.tvName.setText(holder.flexProduct.getName());
        holder.tvName.setCompoundDrawablesWithIntrinsicBounds(null,
                StaticHelper.generateDrawable(holder.flexProduct, holder.tvName.getMaxWidth()),
                null, null);
        //set first character of the products name as text
        holder.tvNameChar.setText(String.valueOf(holder.flexProduct.getName().charAt(0)).toUpperCase());
    }

    @Override
    public int getItemCount() {
        return flexProductList != null ? flexProductList.size() : 0;
    }

    public DragListener getDragInstance() {
        if (listener != null) {
            return new DragListener(listener);
        } else {
            Log.e("ListAdapter", "Listener wasn't initialized!");
            return null;
        }
    }

    public List<FlexProduct> getList() {
        return flexProductList;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View view;
        final TextView tvName;
        final TextView tvNameChar;
        final ImageView ivCart;
        final TextView tvPrice;
        FlexProduct flexProduct;

        ViewHolder(View view){
            super(view);

            this.view = view;
            view.setOnClickListener(this);

            tvName = (TextView) view.findViewById(R.id.tv_listitem_flex_product_name);
            tvNameChar = (TextView) view.findViewById(R.id.tv_listitem_flex_product_name_character);

            ivCart = (ImageView) view.findViewById(R.id.iv_listitem_flex_product_cart);

            tvPrice = (TextView) view.findViewById(R.id.tv_listitem_flex_product_price);
        }

        @Override
        public void onClick(View v) {
            showOptionsPopup();
        }


        private void showOptionsPopup() {
            PopupMenu optionPopup = new PopupMenu(view.getContext(), view);
            optionPopup.getMenuInflater().inflate(R.menu.menu_popup_listitem_flexproduct,
                    optionPopup.getMenu());

            //TODO
            if(flexProduct.isPurchased()){
                optionPopup.getMenu().findItem(R.id.menu_popup_buy_flex_product)
                        .setTitle(R.string.menu_popup_shopping_cart_remove_flex_product);
            }

            optionPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();

                    if(id == R.id.menu_popup_buy_flex_product){
                        if(flexProduct.isPurchased()){
                            listener.productItemPurchase(flexProduct, getAdapterPosition(), false);
                        } else{
                            //FlexProduct flexProduct, int adapterPosition
                            listener.productItemPurchase(flexProduct, getAdapterPosition(), true);
                        }
                    } else if(id == R.id.menu_popup_delete_flex_product){
                        listener.productItemRemove(flexProduct, getAdapterPosition());
                    }
                    return true;
                }
            });

            optionPopup.show();
        }
    }

}
