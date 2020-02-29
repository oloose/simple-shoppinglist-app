package com.android.hoags.shoppinglist.adapters;

import android.content.ClipData;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.hoags.shoppinglist.R;
import com.android.hoags.shoppinglist.helpers.DragListener;
import com.android.hoags.shoppinglist.helpers.OnProductListItemListener;
import com.android.hoags.shoppinglist.helpers.StaticHelper;
import com.android.hoags.shoppinglist.models.Product;

import java.util.List;

/**
 * Created by Oliver on 29-May-17.
 */

public class ProductListRecyclerViewAdapter extends RecyclerView.Adapter<ProductListRecyclerViewAdapter.ViewHolder>
        implements View.OnLongClickListener {
    private List<Product> productList;
    private OnProductListItemListener listener;
    private ViewGroup parentViewGroup;

    public ProductListRecyclerViewAdapter(List<Product> products,
                                          OnProductListItemListener listener){
        this.productList = products;
        this.listener = listener;
    }


    @Override
    public ProductListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parentViewGroup = parent;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductListRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.product = productList.get(position);

        holder.tvName.setText(holder.product.getName());
        holder.tvName.setCompoundDrawablesWithIntrinsicBounds(null,
                StaticHelper.generateDrawable(holder.product, holder.tvName.getMaxWidth()),
                null, null);
        //set first character of the products name as text
        holder.tvNameChar.setText(String.valueOf(holder.product.getName().charAt(0)).toUpperCase());

        holder.relativeLayout.setTag(position);
        holder.relativeLayout.setOnLongClickListener(this);
        holder.relativeLayout.setOnDragListener(new DragListener(listener));
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    @Override
    public boolean onLongClick(View v) {
        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            v.startDragAndDrop(data, shadowBuilder, v, 0);
        } else {
            v.startDrag(data, shadowBuilder, v, 0);
        }
        return true;
    }

    public List<Product> getList() {
        return productList;
    }

    public void updateList(List<Product> list) {
        this.productList = list;
        StaticHelper.sortProductList(productList);
    }

    public DragListener getDragInstance() {
        if (listener != null) {
            return new DragListener(listener);
        } else {
            Log.e("ListAdapter", "Listener wasn't initialized!");
            return null;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        final View view;
        final TextView tvName;
        final TextView tvNameChar;
        RelativeLayout relativeLayout;
        Product product;

        ViewHolder(View view){
            super(view);

            this.view = view;
            //view.setOnDragListener(this);

            tvName = (TextView) view.findViewById(R.id.tv_listitem_flex_product_name);
            tvNameChar = (TextView) view.findViewById(R.id.tv_listitem_flex_product_name_character);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeHolder);
        }
    }
}
