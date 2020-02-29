package com.android.hoags.shoppinglist.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.TextView;

import com.android.hoags.shoppinglist.R;
import com.android.hoags.shoppinglist.fragments.ShoppingListFragment.OnListFragmentInteractionListener;
import com.android.hoags.shoppinglist.models.ShoppingList;
import com.android.hoags.shoppinglist.models.enums.ListType;

import java.util.List;

import static com.android.hoags.shoppinglist.helpers.StaticHelper.decimalFormat;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ShoppingList} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ShoppingListRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListRecyclerViewAdapter.ViewHolder> {

    private final List<ShoppingList> shoppingLists;
    private final OnListFragmentInteractionListener mListener;
    private ListType listType;
    private Context context;

    public ShoppingListRecyclerViewAdapter(List<ShoppingList> items,
                                           OnListFragmentInteractionListener listener,
                                           ListType listType) {
        shoppingLists = items;
        mListener = listener;
        this.listType = listType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_shoppinglist, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.shoppingList = shoppingLists.get(position);

        holder.tvTitle.setText(shoppingLists.get(position).getTitle());

        String tvPriceText = context.getString(R.string.label_price) + " "
                + decimalFormat.format(shoppingLists.get(position).getPrice());
        holder.tvPrice.setText(tvPriceText);

        holder.setFavoriteImageState(holder.shoppingList);
    }

    @Override
    public int getItemCount() {
        return shoppingLists != null ? shoppingLists.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final View view;
        final TextView tvTitle;
        final TextView tvPrice;
        final ImageButton btnFavorite;
        ShoppingList shoppingList;

        ViewHolder(View view) {
            super(view);

            this.view = view;
            view.setOnClickListener(this);

            tvTitle = (TextView) view.findViewById(R.id.tv_listitem_shoppinglist_title);
            tvPrice = (TextView) view.findViewById(R.id.tv_listitem_shoppinglist_price);

            btnFavorite = (ImageButton) view.findViewById(R.id.btn_listitem_shoppinglist_favorite);
            btnFavorite.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();

            if(id == R.id.btn_listitem_shoppinglist_favorite) {
                shoppingList.setFavorite(!shoppingList.isFavorite());

                setFavoriteImageState(shoppingList);

                mListener.updateFavorites(shoppingList);
            } else if(id == R.id.listitem_shoppinglist_view) {
                showMorePopup(getAdapterPosition());
            }
        }

        /**
         * Shows a popup with the option to either show the selected shopping list or to delete it.
         *
         * @param position {int} the items position within the list
         */
        private void showMorePopup(final int position){
            PopupMenu morePopup = new PopupMenu(view.getContext(), view);
            morePopup.getMenuInflater().inflate(R.menu.menu_popup_listitem_shoppinglist,
                    morePopup.getMenu());

            //Setup popup with options
            morePopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.menu_popup_show_shoppingList) {
                        mListener.showShoppingList(shoppingList);
                    } else if (item.getItemId() == R.id.menu_popup_delete_shoppinglist) {
                        mListener.deleteShoppingList(shoppingLists.get(position), position, listType);
                    }
                    return false;
                }
            });

            morePopup.show();
        }

        private void setFavoriteImageState(ShoppingList shoppingList){
            if(shoppingList.isFavorite()){

                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                    btnFavorite.setImageDrawable(btnFavorite.getContext().getDrawable(
                            R.drawable.ic_star_full_24dp));
                } else{
                    btnFavorite.setImageResource(R.drawable.ic_star_full_24dp);
                }
            } else{
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                    btnFavorite.setImageDrawable(btnFavorite.getContext().getDrawable(
                            R.drawable.ic_star_empty_24dp));
                } else{
                    btnFavorite.setImageResource(R.drawable.ic_star_empty_24dp);
                }
            }
        }
    }
}
