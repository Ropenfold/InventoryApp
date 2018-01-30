package com.example.stuart.inventoryapp.data;

/**
 * Created by Stuart on 04/01/2018.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**This is the Contract class which deals with the static variables
 * which populate the database. It also deals with
 *  the content authority, which is the name for the entire content
 *  provider. This is similar to the relationship between a domain name and its website.
 * A convenient string to use for the
 * content authority is the package name for the app, which is guaranteed to be unique on the
 * device.
 **/

public final class InventoryContract {

    public InventoryContract() {

    }

    //Content Authority
    public static final String CONTENT_AUTHORITY = "com.example.stuart.inventoryapp";

    //The CONTENT_AUTHORITY to create the base of all the URI'S which apps will use to
    //contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI'S)
     */

    public static final String PATH_CRICKET_ITEMS = "cricket";

    public static abstract class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CRICKET_ITEMS);

        /**
         * The MIME type of the {@Link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CRICKET_ITEMS;

        /**
         * The MIME type of the {@Link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CRICKET_ITEMS;


        //Name of table
        public static final String TABLE_NAME = "cricket";

        //names of columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "item";
        public static final String COLUMN_ITEM_BRAND = "brand";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMUN_ITEM_PRICE = "price";

    }
}


