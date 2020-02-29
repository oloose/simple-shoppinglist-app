package com.android.hoags.shoppinglist.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;

import com.android.hoags.shoppinglist.R;
import com.android.hoags.shoppinglist.adapters.ProductListRecyclerViewAdapter;
import com.android.hoags.shoppinglist.models.Product;

import java.util.List;

/**
 * Created by ricardo on 09.06.2017.
 */

public class DragListener implements View.OnDragListener {

    private boolean isDropped = false;
    private OnProductListItemListener listener;

    public DragListener(OnProductListItemListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DROP:
                isDropped = true;
                int positionTarget = -1;

                View viewSource = (View) event.getLocalState();
                int viewId = v.getId();
                final int flItem = R.id.relativeHolder;
                final int rvTop = R.id.recycler_view_product_select;
                final int rvBottom = R.id.dropzone;

                switch (viewId) {
                    case flItem:
                    case rvTop:
                    case rvBottom:

                        RecyclerView target;
                        switch (viewId) {
                            case rvTop:
                                target = (RecyclerView) v.getRootView().findViewById(rvTop);
                                break;
                            case rvBottom:
                                target = (RecyclerView) v.getRootView().findViewById(rvBottom);
                                break;
                            default:
                                target = (RecyclerView) v.getParent();
                                positionTarget = (int) v.getTag();
                        }

                        if (viewSource != null) {
                            RecyclerView source = (RecyclerView) viewSource.getParent();

                            ProductListRecyclerViewAdapter adapterSource = (ProductListRecyclerViewAdapter) source.getAdapter();
                            int positionSource = (int) viewSource.getTag();
                            int sourceId = source.getId();

                            Product draggedProduct = adapterSource.getList().get(positionSource);

                            //removes the product from the product list
//                            List<Product> listSource = adapterSource.getList();
                            if (target == v.getRootView().findViewById(rvBottom)) {
                                listener.productItemWasDragged(draggedProduct);
                            }

                            //out-commented because unnecessary, but useful later
//                            listSource.remove(positionSource);
//                            adapterSource.updateList(listSource);
//                            adapterSource.notifyDataSetChanged();


//                            ProductListRecyclerViewAdapter adapterTarget = (ProductListRecyclerViewAdapter) target.getAdapter();
//                            List<Product> customListTarget = adapterTarget.getList();
//                            if (positionTarget >= 0) {
//                                customListTarget.add(positionTarget, dragedProduct);
//                            } else {
//                                customListTarget.add(dragedProduct);
//                            }
//                            adapterTarget.updateList(customListTarget);
//                            adapterTarget.notifyDataSetChanged();


                        }
                        break;
                }
                break;
        }

        if (!isDropped && event.getLocalState() != null) {
            ((View) event.getLocalState()).setVisibility(View.VISIBLE);
        }
        return true;
    }
}