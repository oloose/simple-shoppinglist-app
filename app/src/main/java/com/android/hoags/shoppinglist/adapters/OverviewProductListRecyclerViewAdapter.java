package com.android.hoags.shoppinglist.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hoags.shoppinglist.R;
import com.android.hoags.shoppinglist.helpers.OnProductListOverviewItemListener;
import com.android.hoags.shoppinglist.helpers.StaticHelper;
import com.android.hoags.shoppinglist.models.Product;

import java.util.List;

import static com.android.hoags.shoppinglist.helpers.StaticHelper.decimalFormat;

/**
 * Created by Oliver on 13-Jun-17.
 */

public class OverviewProductListRecyclerViewAdapter extends RecyclerView.Adapter<OverviewProductListRecyclerViewAdapter.ViewHolder> {
    private List<Product> productList;
    private OnProductListOverviewItemListener listener;
    private Context context;

    public OverviewProductListRecyclerViewAdapter(List<Product> productList, OnProductListOverviewItemListener listener){
        this.productList = productList;
        this.listener = listener;
    }

    @Override
    public OverviewProductListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_product_overview,
                parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OverviewProductListRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.product = productList.get(position);

        holder.ivImage.setImageDrawable(StaticHelper.generateDrawable(holder.product, 50));
        holder.tvImageChar.setText(String.valueOf(holder.product.getName().charAt(0)).toUpperCase());
        holder.tvName.setText(holder.product.getName());
        holder.tvSeller.setText(holder.product.getSeller());
        holder.tvPrice.setText(decimalFormat.format(holder.product.getPrice()));

        if(!holder.product.getDescription().equals("")){
            holder.tvDescription.setText(holder.product.getDescription());
        } else{
            holder.labelDescription.setVisibility(View.GONE);
            holder.tvDescription.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View view;
        final ImageView ivImage;
        final TextView tvImageChar;
        final TextView tvName;
        final TextView tvSeller;
        final TextView tvPrice;
        final TextView labelDescription;
        final TextView tvDescription;
        Product product;

        ViewHolder(View view){
            super(view);

            this.view = view;
            this.view.setOnClickListener(this);

            ivImage = (ImageView) view.findViewById(R.id.listitem_overview_product_image);
            tvImageChar = (TextView) view.findViewById(R.id.tv_product_character);
            tvName = (TextView) view.findViewById(R.id.tv_overview_product_name);
            tvSeller = (TextView) view.findViewById(R.id.tv_overview_product_seller);
            tvPrice = (TextView) view.findViewById(R.id.tv_overview_product_price);
            labelDescription = (TextView) view.findViewById(R.id.tv_label_description);
            tvDescription = (TextView) view.findViewById(R.id.tv_overview_product_description);
        }

        @Override
        public void onClick(View v) {
            int id = view.getId();

            if(id == R.id.listitem_overview_product_view){
                showOptionsPopup(getAdapterPosition());
            }
        }

        private void showOptionsPopup(final int position) {
            PopupMenu optionPopup = new PopupMenu(view.getContext(), view);
            optionPopup.getMenuInflater().inflate(R.menu.menu_popup_listitem_product,
                    optionPopup.getMenu());

            //Setup popup with options
            optionPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.menu_popup_edit_product) {
                        listener.onItemEdit(product, position);
                    } else if (item.getItemId() == R.id.menu_popup_delete_product) {
                        listener.onItemRemoved(product, position);
                    }
                    return false;
                }
            });

            optionPopup.show();
        }
    }
}
