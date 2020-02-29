package com.android.hoags.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.hoags.shoppinglist.R;
import com.android.hoags.shoppinglist.fragments.ShoppingListFragment;
import com.android.hoags.shoppinglist.helpers.StaticHelper;
import com.android.hoags.shoppinglist.models.Product;
import com.android.hoags.shoppinglist.models.ShoppingList;
import com.android.hoags.shoppinglist.models.enums.ListType;
import com.android.hoags.shoppinglist.persistence.PersistenceService;


public class MainActivity extends AppCompatActivity implements 
        ShoppingListFragment.OnListFragmentInteractionListener,
        View.OnClickListener{

    public static final String EXTRA_SHOPPINGLIST_ID = "EXTRA_SHOPPINGLIST_ID";

    /*
     * UI
     */
    private CoordinatorLayout coordinatorLayout;

    /*
     * Fragments
     */
    private ShoppingListFragment recentShoppingListsFragment;
    private ShoppingListFragment favoriteShoppingListFragment;

    private PersistenceService persistenceService;

    private class ShoppingListFragmentAdapter extends FragmentPagerAdapter {
        private int FRAGMENT_COUNT = 2;
        private Context context;

        ShoppingListFragmentAdapter(FragmentManager manager, Context context){
            super(manager);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    recentShoppingListsFragment = ShoppingListFragment.newInstance(
                            context.getResources().getStringArray(R.array.fragment_titles)[position],
                            ListType.ALL_SHOPPINGLISTS);
                    return recentShoppingListsFragment;
                case 1:
                    favoriteShoppingListFragment = ShoppingListFragment.newInstance(
                            context.getResources().getStringArray(R.array.fragment_titles)[position],
                            ListType.FAVORITE_SHOPPINGLISTS);
                    return favoriteShoppingListFragment;
                default:
                    //something has gone wrong
                    return null;
            }
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return context.getResources().getStringArray(R.array.fragment_titles)[position];
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Initialize UI - Components
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        ShoppingListFragmentAdapter fragmentAdapter = new ShoppingListFragmentAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        ViewPager fragmentViewPager = (ViewPager) findViewById(R.id.container);
        fragmentViewPager.setAdapter(fragmentAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(fragmentViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_start_shoppinglist_activity);
        fab.setOnClickListener(this);

        /*
         * Database access
         */
        persistenceService = new PersistenceService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.mitem_product_overview){
            startProductOverviewActivity();
            return true;
        }
        return false;
    }

    /**
     * Starts a shopping list activity.
     *
     * @param shoppingListId If an existing shopping list is opened, at the id of the list, else set
     *                       shoppingListId null or -1
     */
    private void startShoppingListActivity(int shoppingListId){
        Intent intent = new Intent(this, ShoppingListActivity.class);
        intent.putExtra(EXTRA_SHOPPINGLIST_ID, shoppingListId);
        startActivity(intent);
    }

    private void startProductOverviewActivity(){
        startActivity(new Intent(this, ProductOverviewActivity.class));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.fab_start_shoppinglist_activity){
            startShoppingListActivity(-1);
        }
    }

    @Override
    public void showShoppingList(ShoppingList shoppingList) {
        startShoppingListActivity(shoppingList.getId());
    }

    @Override
    public void deleteShoppingList(ShoppingList shoppingList, int adapterPosition, ListType listType) {
        persistenceService.deleteShoppingList(shoppingList);

        //update list after removing an item
        recentShoppingListsFragment.removeShoppingList(shoppingList, adapterPosition);
        favoriteShoppingListFragment.removeShoppingList(shoppingList, adapterPosition);

        if(shoppingList.getTitle() != null){
            StaticHelper.snackbar(shoppingList.getTitle() + " " + getString(R.string.snackbar_message_item_deleted), coordinatorLayout);
        } else{
            StaticHelper.snackbar(getString(R.string.snackbar_message_deleted_shopping_list), coordinatorLayout);
        }
    }

    @Override
    public void updateFavorites(ShoppingList shoppingList) {
        persistenceService.updateShoppingList(shoppingList);

        recentShoppingListsFragment.updateFavorites(shoppingList);
        favoriteShoppingListFragment.updateFavorites(shoppingList);

        if(shoppingList.isFavorite()){
            StaticHelper.snackbar(shoppingList.getTitle() + " "
                    + getString(R.string.snackbar_message_item_favored), coordinatorLayout);
        } else{
            StaticHelper.snackbar(shoppingList.getTitle() + " "
                    + getString(R.string.snackbar_message_item_unfavored), coordinatorLayout);
        }
    }
}
