package com.android.hoags.shoppinglist.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hoags.shoppinglist.R;
import com.android.hoags.shoppinglist.helpers.PrepareActivityEnum;
import com.android.hoags.shoppinglist.helpers.StaticHelper;
import com.android.hoags.shoppinglist.models.Product;
import com.android.hoags.shoppinglist.persistence.PersistenceService;

import static com.android.hoags.shoppinglist.helpers.StaticHelper.decimalFormat;

public class ProductDialogActivity extends Activity implements View.OnClickListener {
    public static final String EXTRA_NEW_PRODUCT_NAME = "EXTRA_NEW_PRODUCT_NAME";
    public static final String EXTRA_EDIT_PRODUCT_ID = "EXTRA_EDIT_PRODUCT_ID";
    public static final String EXTRA_EDIT_PRODUCT_POSITION_IN_LIST = "EXTRA_EDIT_PRODUCT_POSITION_IN_LIST";
    /*
         * MISC
         */
    private PersistenceService persistenceService;
    private Product dialogProduct;
    private int productId;
    private int productPostionInList;
    private boolean isFlexProductBuyRequest;

    /*
     * UI
     */
    private TextView tvCharacter;

    private EditText etName;
    private EditText etSeller;
    private EditText etDescription;
    private EditText etPrice;
    private ImageView ivIcon;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(true);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_product_dialog);

        //Activity -> Dialog look and feel
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //Full Width
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //transparent background

        persistenceService = new PersistenceService(this);
        //get extras if extras exist
        productId = getIntent().getIntExtra(ProductOverviewActivity.EXTRA_EDIT_PRODUCT_ID, -1);
        productPostionInList = getIntent().getIntExtra(ProductOverviewActivity.EXTRA_EDIT_PRODUCT_POSITION_IN_LIST, -1);
        isFlexProductBuyRequest = getIntent().getBooleanExtra(ShoppingListActivity.EXTRA_EDIT_PRODUCT_PRICE_ONLY, false);

        //initialize ui
        btnAdd = (Button) findViewById(R.id.btn_product_dialog_add);
        btnAdd.setOnClickListener(this);

        Button btnCancel = (Button) findViewById(R.id.btn_product_dialog_cancel);
        btnCancel.setOnClickListener(this);

        tvCharacter = (TextView) findViewById(R.id.tv_product_character);
        ivIcon = (ImageView) findViewById(R.id.listitem_overview_product_image);

        etName = (EditText) findViewById(R.id.tv_overview_product_name);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = etName.getText().toString();
                if(text.length() > 0){
                    tvCharacter.setText(String.valueOf(text.charAt(0)).toUpperCase());
                }
            }
        });

        etSeller = (EditText) findViewById(R.id.tv_overview_product_seller);
        etPrice = (EditText) findViewById(R.id.tv_overview_product_price);
        etDescription = (EditText) findViewById(R.id.tv_overview_product_description);


        if(productId != -1){
            prepareActivity(PrepareActivityEnum.FOR_EDIT);
        } else{
            prepareActivity(PrepareActivityEnum.FOR_NEW);
        }
    }

    /**
     * Prepare activity for a specific action (editing a product, creating a new product, etc.).
     *
     * @param forWhat {@link PrepareActivityEnum#FOR_EDIT} {@link PrepareActivityEnum#FOR_NEW}
     */
    private void prepareActivity(PrepareActivityEnum forWhat) {
        if(forWhat == PrepareActivityEnum.FOR_EDIT){
            dialogProduct = persistenceService.getProductById(productId);
            prepareDialogViews(dialogProduct);
        } else if(forWhat == PrepareActivityEnum.FOR_NEW){
            dialogProduct = new Product();
            dialogProduct.setColor();
            ivIcon.setImageDrawable(StaticHelper.generateDrawable(dialogProduct,
                    Math.round(getResources().getDimension(R.dimen.product_image_size))));
        }
    }

    /**
     * Set the view values with values of the given product.
     *
     * @param productDisplayedInDialog Product to get the values from.
     */
    private void prepareDialogViews(Product productDisplayedInDialog) {
        ivIcon.setImageDrawable(StaticHelper.generateDrawable(dialogProduct,
                Math.round(getResources().getDimension(R.dimen.product_image_size))));
        tvCharacter.setText(String.valueOf(productDisplayedInDialog.getName().charAt(0)));

        etName.setText(productDisplayedInDialog.getName());
        etSeller.setText(productDisplayedInDialog.getSeller());
        etPrice.setText(decimalFormat.format(productDisplayedInDialog.getPrice()));
        etDescription.setText(productDisplayedInDialog.getDescription());

        btnAdd.setText(R.string.btn_dialog_button_edit_confirm);

        if(isFlexProductBuyRequest){
            // if is buy request (dialog opened only to change the product price) disable all views
            // except of etPrice
            etName.setEnabled(false);
            etSeller.setEnabled(false);
            etDescription.setEnabled(false);
            etPrice.requestFocus();
            etPrice.selectAll();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.btn_product_dialog_add){
            if (!etName.getText().toString().trim().equals("")) {
                /* build new product from input */
                //set name
                dialogProduct.setName(etName.getText().toString());

                //set price
                if (!etPrice.getText().toString().trim().equals("")) {
                    dialogProduct.setPrice(Double.parseDouble(etPrice.getText().toString()));
                }

                //set description
                if (!etDescription.getText().toString().equals("")) {
                    dialogProduct.setDescription(etDescription.getText().toString());
                }

                //set seller
                if (!etSeller.getText().toString().equals("")) {
                    dialogProduct.setSeller(etSeller.getText().toString());
                }

                //save product in database
                persistenceService.saveProduct(dialogProduct);

                setResult(RESULT_OK, intentConfirmDialog());
                finish();
            } else {
                etName.setError(getString(R.string.error_empty_product_name));
            }

        } else if(id == R.id.btn_product_dialog_cancel){
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    /**
     * Build an intent depending on if the dialog activity was started to edit an existing product, or to
     * create a new product. If the dialog is to edit a product, then add the products id, and position in product
     * list as extras. If the dialog is to create a new product, then add the new products name as
     * an extra.
     *
     * @return The created intent.
     */
    private Intent intentConfirmDialog(){
        Intent sendIntent = new Intent(this, ProductDialogActivity.class);

        if(productId != -1){
            sendIntent.putExtra(EXTRA_EDIT_PRODUCT_ID, productId);
            sendIntent.putExtra(EXTRA_EDIT_PRODUCT_POSITION_IN_LIST, productPostionInList);
        } else {
            //send product name for query in ShoppingListActivity.class after finish
            sendIntent.putExtra(EXTRA_NEW_PRODUCT_NAME, dialogProduct.getName());
        }

        return sendIntent;
    }
}



