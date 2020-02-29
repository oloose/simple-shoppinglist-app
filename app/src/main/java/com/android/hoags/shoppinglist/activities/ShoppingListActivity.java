package com.android.hoags.shoppinglist.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.hoags.shoppinglist.R;
import com.android.hoags.shoppinglist.adapters.FlexProductListRecyclerViewAdapter;
import com.android.hoags.shoppinglist.adapters.ProductListRecyclerViewAdapter;
import com.android.hoags.shoppinglist.helpers.OnProductListItemListener;
import com.android.hoags.shoppinglist.helpers.StaticHelper;
import com.android.hoags.shoppinglist.models.FlexProduct;
import com.android.hoags.shoppinglist.models.Product;
import com.android.hoags.shoppinglist.models.ShoppingList;
import com.android.hoags.shoppinglist.persistence.PersistenceService;

import java.util.ArrayList;
import java.util.List;

import static com.android.hoags.shoppinglist.helpers.StaticHelper.decimalFormat;

public class ShoppingListActivity extends AppCompatActivity implements OnProductListItemListener, View.OnClickListener {
    private static final int NEW_PRODUCT_REQUEST = 1;
    private static final int EDIT_PRODUCT_PRICE_REQUEST = 2;
    public static final String EXTRA_EDIT_PRODUCT_ID = "EXTRA_EDIT_PRODUCT_ID";
    public static final String EXTRA_EDIT_PRODUCT_PRICE_ONLY = "EXTRA_EDIT_PRODUCT_PRICE_ONLY";
    /*
     * MISC
     */
    private int shoppingListId;
    private PersistenceService persistenceService;
    private List<Product> productList;  //all product which can be added to the cart
    private List<FlexProduct> flexProductList; //the 'cart' items of the shoppinglist
    private ShoppingList shoppingList;  //shoppinglist edited in this activity
    private ProductListRecyclerViewAdapter productAdapter;
    private FlexProductListRecyclerViewAdapter flexProductAdapter;
    private boolean isDeleted = false;  // if the shoppinglist was deleted set true
    private boolean isBackPressed = false; // wherever the user used back navigation set true

    /*
     * UI
     */
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView rvProductList;
    private RecyclerView rvShoppingListDropzone;
    private TextView tvCartPrice;
    private TextView tvShoppingListPrice;
    private Button btnFirstProduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout_shoppinglist_activity);

        //TextViews to display prices (must be before initializing productList and flexProductList)
        tvCartPrice = (TextView) findViewById(R.id.tv_cart_price);
        tvShoppingListPrice = (TextView) findViewById(R.id.tv_shopping_list_price);

        //Database access
        persistenceService = new PersistenceService(this);

        //if loaded shoppinglist get id from intent
        shoppingListId = getIntent().getIntExtra(MainActivity.EXTRA_SHOPPINGLIST_ID, -1);
        setupLoadedShoppingList();

        //Get all products
        productList = new ArrayList<>(persistenceService.getAllProducts());
        StaticHelper.sortProductList(productList);

        //Buttons (must be after 'get all Products')
        btnFirstProduct = (Button) findViewById(R.id.btn_add_first_product);
        btnFirstProduct.setOnClickListener(this);
        if(productList.isEmpty()){
            btnFirstProduct.setVisibility(View.VISIBLE);
        }

        //RecyclerView productList
        rvProductList = (RecyclerView) findViewById(R.id.recycler_view_product_select);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvProductList.setLayoutManager(manager);

        productAdapter = new ProductListRecyclerViewAdapter(productList, this);
        rvProductList.setAdapter(productAdapter);

        //RecyclerView dropzone
        rvShoppingListDropzone = (RecyclerView) findViewById(R.id.dropzone);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        rvShoppingListDropzone.setLayoutManager(gridLayoutManager);

        flexProductAdapter = new FlexProductListRecyclerViewAdapter(flexProductList, this);
        rvShoppingListDropzone.setAdapter(flexProductAdapter);
        rvShoppingListDropzone.setOnDragListener(flexProductAdapter.getDragInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shoppinglist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.menu_add_new_product){
            addNewProductDialog();
            return true;
        } else if(id == R.id.menu_save_shoppinglist){
            saveShoppingList();
            return true;
        } else if(id == R.id.menu_delete_shoppingList){
            //delete shoppinglist from database and close activity
            persistenceService.deleteShoppingList(shoppingList);
            isDeleted = true;
            finish();
        } else if(id == R.id.menu_clear_shopping_cart){
            clearShoppingCart();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == NEW_PRODUCT_REQUEST){
            if(resultCode == RESULT_OK){
                String newProductName = data.getStringExtra(ProductDialogActivity.EXTRA_NEW_PRODUCT_NAME);
                updateProductList(newProductName);
                StaticHelper.snackbar(newProductName + getString(R.string.snackbar_message_added_product), coordinatorLayout);
            }
        }

        if(requestCode == EDIT_PRODUCT_PRICE_REQUEST){
            if(resultCode == RESULT_OK){
                int editProductId = data.getIntExtra(ProductDialogActivity.EXTRA_EDIT_PRODUCT_ID, -1);

                // update all flex products with the new price of the underlying product
                Product updateParentProduct = persistenceService.getProductById(editProductId);
                persistenceService.updateAllFlexProductsByProduct(updateParentProduct);

                //update flex product list
                updateFlexProductsInCart(updateParentProduct);
                flexProductAdapter.notifyDataSetChanged();

                // update product list
                productList = new ArrayList<>(persistenceService.getAllProducts());
                productAdapter = new ProductListRecyclerViewAdapter(productList, this);
                rvProductList.setAdapter(productAdapter);
                productAdapter.notifyDataSetChanged();

                setPrices();
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(!isDeleted){
            shoppingList.setLastUsed();
            persistenceService.saveShoppingList(shoppingList);
        }
    }

    @Override
    public void productItemWasDragged(Product product) {
        FlexProduct flexProduct = new FlexProduct(product, shoppingList);
        persistenceService.saveFlexProduct(flexProduct);
        flexProductList.add(flexProduct);

        flexProductAdapter.notifyDataSetChanged();

        StaticHelper.snackbar(product.getName() +
                getString(R.string.snackbar_message_added_product_to_shoppingList), coordinatorLayout);
        setPrices();
    }

    @Override
    public void productItemRemove(FlexProduct flexProduct, int position) {
        //remove FlexProduct from shopping list and database
        flexProductList.remove(flexProduct);
        persistenceService.deleteFlexProduct(flexProduct);

        flexProductAdapter.notifyItemRemoved(position);

        StaticHelper.snackbar(flexProduct.getName() + getString(R.string.snackbar_message_removed_item_from_shopping_list),
                coordinatorLayout);
        setPrices();
    }

    @Override
    public void productItemPurchase(FlexProduct flexProduct, int position, boolean isBuy) {
        if(isBuy){
            //open dialog to show flex product details and possiblity to adjust the product price
            editProductDialog(flexProduct.getProduct().getId());
        } else {
            StaticHelper.snackbar(flexProduct.getName() +
                            getString(R.string.snackbar_message_product_purchase_reverse),
                    coordinatorLayout);
            flexProductAdapter.notifyItemChanged(position);
        }

        flexProduct.setPurchased(!flexProduct.isPurchased());
        persistenceService.updateFlexProduct(flexProduct);
        setPrices();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.btn_add_first_product){
            addNewProductDialog();
        }
    }


    /**
     * Iterates over flexProductList to determine which flexProducts are affected by the changes in
     * updateParentProduct and updates thus flexProducts.
     *
     * @param updateParentProduct The changed product
     */
    private void updateFlexProductsInCart(Product updateParentProduct) {
        for(FlexProduct flexProduct : flexProductList){
            if(flexProduct.getProduct().getId() == updateParentProduct.getId()){
                flexProduct.updateProduct(updateParentProduct);
            }
        }
    }

    /**
     * Sets all FlexProducts in flexProductList to isPurchased = false.
     */
    private void clearShoppingCart() {
        for(FlexProduct flexProduct : flexProductList){
            flexProduct.setPurchased(false);
            persistenceService.updateFlexProduct(flexProduct);
            flexProductAdapter.notifyDataSetChanged();
        }

        setPrices();
    }

    /**
     * Calculates the overall price for all purchased products and the price for the whole
     * shopping list. Then sets the calculated values as text in the corresponding TextViews
     */
    private void setPrices(){
        double cartPrice = 0.00;
        double shoppingListPrice = 0.00;

        for (FlexProduct flexProduct : flexProductList){
            if(flexProduct.isPurchased()){
                cartPrice += flexProduct.getPrice();
            }
            shoppingListPrice += flexProduct.getPrice();
        }

        tvCartPrice.setText(decimalFormat.format(cartPrice));
        tvShoppingListPrice.setText(decimalFormat.format(shoppingListPrice));
    }

    /**
     * Adds the newly created product to the product list and updates the productAdapter.
     *
     * @param newProductName Name of the newly create product.
     */
    private void updateProductList(String newProductName) {
        productList.add(persistenceService.getProductByName(newProductName));
        StaticHelper.sortProductList(productList);

        // hide btnFirstProduct if productList is not empty
        if(productList.isEmpty()){
            btnFirstProduct.setVisibility(View.VISIBLE);
        } else{
            btnFirstProduct.setVisibility(View.GONE);
        }

        //refresh productAdapter
        productAdapter.notifyDataSetChanged();
    }

    /**
     * If an existing shopping list is opened, prepare the activity with the loaded shopping lists
     * content.
     */
    private void setupLoadedShoppingList(){
        if(shoppingListId != -1){
            shoppingList = persistenceService.getShoppingListById(shoppingListId);
            //ignore, supportActionBar is set in onCreate!
            getSupportActionBar().setTitle(shoppingList.getTitle());

            //get products in shoppinglist
            flexProductList = new ArrayList<>(shoppingList.getProductList());
        } else {
            shoppingList = new ShoppingList();
            persistenceService.saveShoppingList(shoppingList);

            flexProductList = new ArrayList<>();
            //ignore, supportActionBar is set in onCreate!
            getSupportActionBar().setTitle(getString(R.string.title_default_shoppinglist));
        }
        shoppingList.setLastUsed();
        setPrices();
    }

    /**
     * Open a dialog to add a new product.
     */
    private void addNewProductDialog() {
        startActivityForResult(newProductIntent(), NEW_PRODUCT_REQUEST);
    }

    private Intent newProductIntent(){
        return new Intent(this, ProductDialogActivity.class);
    }

    /**
     * Show a dialog whre a name for a shoppinglist can be entered and return true if the dialog was
     * successfull or false if the dialog was canceled.
     */
    private void saveShoppingList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText etNameShoppingList = new EditText(this);
        if(shoppingList.getTitle() != null){
            etNameShoppingList.setText(shoppingList.getTitle());
        }
        //select the edit text
        etNameShoppingList.selectAll();
        etNameShoppingList.requestFocus();

        builder.setTitle(R.string.title_dialog_name_shoppinglist);
        builder.setView(etNameShoppingList);

        //set to null, onClick is overridden later
        builder.setPositiveButton(getString(R.string.btn_save), null);

        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StaticHelper.snackbar(getString(R.string.snackbar_message_action_canceled), coordinatorLayout);
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        // Override on positive button click to make it possible to show a error message if no
        // shopping list name was entered
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //get name from dialog and set it as the shopping lists name
                        String title = etNameShoppingList.getText().toString();
                        if(!title.trim().equals("")){
                            shoppingList.setTitle(etNameShoppingList.getText().toString());
                            //change activity title
                            getSupportActionBar().setTitle(title);

                            //save shopping list in database
                            shoppingList.setLastUsed();
                            persistenceService.saveShoppingList(shoppingList);
                            StaticHelper.snackbar(getString(R.string.snackbar_message_saved_shopping_list), coordinatorLayout);

                            dialog.dismiss();
                        } else {
                            //show error when no name was entered
                            etNameShoppingList.setError(getString(R.string.snackbar_message_error_shopping_list_name_required));
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    /**
     * Open ProductDialogActivity to change the products price if needed.
     *
     * @param editProductId Id of the product to edit/change the price
     */
    private void editProductDialog(int editProductId){
        startActivityForResult(editProductItent(editProductId), EDIT_PRODUCT_PRICE_REQUEST);
    }

    /**
     * Builds the intent to open a ProductDialogActivity and sets productId as extra and wherever
     * the dialog should only be opened to change the products price.
     *
     * @param editProductId Id of the product to change the price of.
     * @return
     */
    private Intent editProductItent(int editProductId){
        Intent intent = new Intent(this, ProductDialogActivity.class);
        intent.putExtra(EXTRA_EDIT_PRODUCT_ID, editProductId);
        intent.putExtra(EXTRA_EDIT_PRODUCT_PRICE_ONLY, true);
        return intent;
    }
}
