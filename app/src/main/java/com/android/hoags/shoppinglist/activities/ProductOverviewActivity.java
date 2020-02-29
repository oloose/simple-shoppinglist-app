package com.android.hoags.shoppinglist.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.hoags.shoppinglist.R;
import com.android.hoags.shoppinglist.adapters.OverviewProductListRecyclerViewAdapter;
import com.android.hoags.shoppinglist.helpers.OnProductListOverviewItemListener;
import com.android.hoags.shoppinglist.helpers.StaticHelper;
import com.android.hoags.shoppinglist.models.Product;
import com.android.hoags.shoppinglist.persistence.PersistenceService;

import java.util.ArrayList;
import java.util.List;

public class ProductOverviewActivity extends AppCompatActivity implements View.OnClickListener,
        OnProductListOverviewItemListener{
    private static final int NEW_PRODUCT_REQUEST = 1;
    private static final int EDIT_PRODUCT_REQUEST = 2;
    public static final String EXTRA_EDIT_PRODUCT_ID = "EXTRA_EDIT_PRODUCT_ID";
    public static final String EXTRA_EDIT_PRODUCT_POSITION_IN_LIST = "EXTRA_EDIT_PRODUCT_POSITION_IN_LIST";

    /*
     * MISC
    */
    private List<Product> productList;
    private PersistenceService persistenceService;

    /*
     * UI
     */
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView rvProductList;
    private OverviewProductListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout_product_overview);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_product);
        fab.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Database
        persistenceService = new PersistenceService(this);
        productList = new ArrayList<>(persistenceService.getAllProducts());

        //RecyclerView
        rvProductList = (RecyclerView) findViewById(R.id.rv_product_overview);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvProductList.setLayoutManager(manager);

        //RecyclerView adapter
        adapter = new OverviewProductListRecyclerViewAdapter(productList, this);
        rvProductList.setAdapter(adapter);

        //decoration for recyclerview
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                rvProductList.getContext(), manager.getOrientation());
        rvProductList.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.fab_add_product){
            addNewProductDialog();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == NEW_PRODUCT_REQUEST){
            if(resultCode == RESULT_OK){
                String newProductName = data.getStringExtra(ProductDialogActivity.EXTRA_NEW_PRODUCT_NAME);
                //add new product to list and update adapter
                onItemAdded(newProductName);
            } else if (resultCode == RESULT_CANCELED){
                StaticHelper.snackbar(getString(R.string.snackbar_message_action_canceled), coordinatorLayout);
            }
        }

        if(requestCode == EDIT_PRODUCT_REQUEST){
            if(resultCode == RESULT_OK){
                int editProductId = data.getIntExtra(ProductDialogActivity.EXTRA_EDIT_PRODUCT_ID, -1);
                int editProductPosition = data.getIntExtra(ProductDialogActivity.EXTRA_EDIT_PRODUCT_POSITION_IN_LIST, -1);

                //update product and update adapter
                onItemEdited(editProductId, editProductPosition);
            } else if (resultCode == RESULT_CANCELED){
                StaticHelper.snackbar(getString(R.string.snackbar_message_action_canceled), coordinatorLayout);
            }

        }
    }

    /**
     * Open a dialog to add a new product.
     */
    private void addNewProductDialog() {
        startActivityForResult(newProductIntent(), NEW_PRODUCT_REQUEST);
    }

    private void editProductDialog(int editProductId, int positionInList){
        startActivityForResult(editProductItent(editProductId, positionInList), EDIT_PRODUCT_REQUEST);
    }

    private Intent editProductItent(int editProductId, int positionInList){
        Intent intent = new Intent(this, ProductDialogActivity.class);
        intent.putExtra(EXTRA_EDIT_PRODUCT_ID, editProductId);
        intent.putExtra(EXTRA_EDIT_PRODUCT_POSITION_IN_LIST, positionInList);
        return intent;
    }

    private Intent newProductIntent(){
        return new Intent(this, ProductDialogActivity.class);
    }

    @Override
    public void onItemRemoved(final Product product, final int position) {
        if(persistenceService.existsFlexProductWithProduct(product)){
            //show verification dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(product.getName() + getString(R.string.dialog_headline_remove_product));

            builder.setPositiveButton(getString(R.string.dialog_button_delete_product), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeProduct(product, position);
                }
            });

            builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        } else{
            removeProduct(product, position);
        }
    }

    private void removeProduct(Product product, int position){
        productList.remove(product);
        persistenceService.deleteProduct(product);
        adapter.notifyItemRemoved(position);

        StaticHelper.snackbar(product.getName() + getString(R.string.snackbar_message_deleted), coordinatorLayout);
    }

//    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//    final EditText etNameShoppingList = new EditText(this);
//        if(shoppingList.getTitle() != null){
//        etNameShoppingList.setText(shoppingList.getTitle());
//    }
//    //select the edit text
//        etNameShoppingList.selectAll();
//        etNameShoppingList.requestFocus();
//
//        builder.setTitle(R.string.title_dialog_name_shoppinglist);
//        builder.setView(etNameShoppingList);
//
//    //set to null, onClick is overridden later
//        builder.setPositiveButton(getString(R.string.btn_save), null);
//
//        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            StaticHelper.snackbar(getString(R.string.snackbar_message_action_canceled), coordinatorLayout);
//            dialog.cancel();
//        }
//    });

    @Override
    public void onItemEdit(Product product, int positionInList) {
        editProductDialog(product.getId(), positionInList);
    }

    @Override
    public void onItemEdited(int productId, int position) {
        Product editedProduct = persistenceService.getProductById(productId);
        productList.get(position).updateProduct(editedProduct);
        adapter.notifyItemChanged(position, productList.get(position));

        StaticHelper.snackbar(getString(R.string.snackbar_message_changes_for)
                + editedProduct.getName()
                + getString(R.string.snackbar_message_edit_confirm), coordinatorLayout);
    }

    @Override
    public void onItemAdded(String productName) {
        productList.add(persistenceService.getProductByName(productName));
        StaticHelper.sortProductList(productList);

        adapter.notifyDataSetChanged();
        StaticHelper.snackbar(productName + getString(R.string.snackbar_message_added_product), coordinatorLayout);
    }
}
