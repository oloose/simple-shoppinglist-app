package com.android.hoags.shoppinglist.models.enums;

/**
 * Created by Oliver on 20-May-17.
 */

public enum ListType {
    ALL_SHOPPINGLISTS,
    FAVORITE_SHOPPINGLISTS;

    public static ListType getByOrdinal(int ordinal){
        switch (ordinal){
            case 0:
                return ALL_SHOPPINGLISTS;
            case 1:
                return FAVORITE_SHOPPINGLISTS;
            default:
                return ALL_SHOPPINGLISTS;
        }
    }
}
