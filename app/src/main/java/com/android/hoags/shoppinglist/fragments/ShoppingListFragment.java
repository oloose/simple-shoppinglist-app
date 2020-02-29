package com.android.hoags.shoppinglist.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.hoags.shoppinglist.R;
import com.android.hoags.shoppinglist.adapters.ShoppingListRecyclerViewAdapter;
import com.android.hoags.shoppinglist.models.ShoppingList;
import com.android.hoags.shoppinglist.models.enums.ListType;
import com.android.hoags.shoppinglist.persistence.PersistenceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ShoppingListFragment extends Fragment {
    /*
     * Fragment initialization
     */
    private static final String ARG_FRAGMENT_TITLE = "fragment-title";
    private static final String ARG_FRAGMENT_LIST_TYPE = "fragment-listtype";
    private String fragmentTitle;
    private ListType fragmentListType;
    private RecyclerView recyclerView;

    /*
     * Misc
     */
    private ArrayList<ShoppingList> shoppingLists;
    private PersistenceService persistenceService;
    private ShoppingListRecyclerViewAdapter adapter;


    private OnListFragmentInteractionListener listenerRecentFragment;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShoppingListFragment() {
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void showShoppingList(ShoppingList shoppingList);
        void deleteShoppingList(ShoppingList shoppingList, int position, ListType listType);
        void updateFavorites(ShoppingList shoppingList);
    }

    public static ShoppingListFragment newInstance(String title, ListType type) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TITLE, title);
        args.putInt(ARG_FRAGMENT_LIST_TYPE, type.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            fragmentTitle = getArguments().getString(ARG_FRAGMENT_TITLE);
            fragmentListType = ListType.getByOrdinal(getArguments().getInt(ARG_FRAGMENT_LIST_TYPE));
        }

        /*
         * Database access
         */
        persistenceService = new PersistenceService(this.getContext());
    }

    @Override
    public void onResume(){
        super.onResume();
        shoppingLists = loadShoppingLists();
        sortShoppingList();
        adapter = new ShoppingListRecyclerViewAdapter(shoppingLists, listenerRecentFragment,
                fragmentListType);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppinglist_list, container, false);

        // Set the adapter
        if (view instanceof CardView) {
            Context context = view.getContext();

            //recyclerview
            recyclerView = (RecyclerView) view.findViewById(R.id.list_shopping_lists);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);

            //load all shoppingLists, has to be done before onCreateView
            shoppingLists = loadShoppingLists();

            //adapter
            sortShoppingList();
            adapter = new ShoppingListRecyclerViewAdapter(shoppingLists,
                    listenerRecentFragment, fragmentListType) ;
            recyclerView.setAdapter(adapter);

            //decoration for recyclerview
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                    recyclerView.getContext(), layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

            adapter.notifyDataSetChanged();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            listenerRecentFragment = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listenerRecentFragment = null;
    }

    /**
     * Load a list of shopping lists depending on the fragment type (ListType.ALL_SHOPPINGLISTS ||
     * ListType.FAVORITES).
     *
     * @return {ArrayList<ShoppingList>} All found shopping lists
     *
     * @see ListType
     */
    private ArrayList<ShoppingList> loadShoppingLists(){
        if(fragmentListType == ListType.ALL_SHOPPINGLISTS){
            return new ArrayList<>(persistenceService.getAllShoppingLists());
        } else{
            return new ArrayList<>(persistenceService.getAllShoppingListsFavorites());
        }
    }

    /**
     * Notifies the adapter that an item (a shoppinglist) was removed and removes the item from the
     * hooked arraylist in the adapter.
     *
     * @param shoppingList {ShoppingList} Removed shopping list
     * @param atPosition {int} Position of the removed item within the list
     */
    public void removeShoppingList(ShoppingList shoppingList, int atPosition){
        int id = shoppingList.getId();

        for(int i = 0; i < shoppingLists.size(); i++){
            ShoppingList list = shoppingLists.get(i);
            if(list.getId() == id){
                shoppingLists.remove(i);
                adapter.notifyItemRemoved(i);
                return;
            }
        }
    }

    /**
     * Notifies the adapter that an item favorite state has changed. If the item is no longer a
     * favorite, it will be removed from the list. If it is marked as a favorite, it will be add to
     * the list.
     *
     * @param shoppingList {{@link ShoppingList}} Changed shopping list
     */
    public void updateFavorites(ShoppingList shoppingList) {
        int id = shoppingList.getId();
        boolean favored = shoppingList.isFavorite();

        if(favored){
            if(fragmentListType == ListType.FAVORITE_SHOPPINGLISTS){
                shoppingLists.add(shoppingList);
            }
        } else{
            for(int i = 0; i < shoppingLists.size(); i++){
                ShoppingList list = shoppingLists.get(i);
                if(list.getId() == id){
                    shoppingLists.remove(i);
                    if(fragmentListType == ListType.ALL_SHOPPINGLISTS){
                        shoppingLists.add(i, shoppingList);
                        adapter.notifyItemChanged(i);
                    } else if(fragmentListType == ListType.FAVORITE_SHOPPINGLISTS){
                        adapter.notifyItemRemoved(i);
                    }
                    return;
                }
            }
        }
        sortShoppingList();
        adapter.notifyDataSetChanged();
    }

    private void sortShoppingList(){
        //sort by date with newest as first
        Collections.sort(shoppingLists, new Comparator<ShoppingList>() {
            @Override
            public int compare(ShoppingList o1, ShoppingList o2) {
                return o1.getLastUsed().compareTo(o2.getLastUsed()) * -1;
            }
        });
    }
}
