package com.example.stuart.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.stuart.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Stuart on 10/01/2018.
 */

public class ItemCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@Link ItemCursorAdapter}.
     *
     * @param context   The context
     * @param c         The cursor from which to get the data.
     */

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to
     *                the correct row.
     * @param parent  ViewGroup used for the newView
     * @return
     */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     *  This method binds the pet data (in the current row pointed to by cursor) to the given
     *  list item layout. For Example, the name for the current pet can be set on the
     *  name TextView in the list item layout.
     *
     *  @param  view     Existing view, returned earlier by newView() method
     *  @param  context  app context
     *  @param  cursor   The cursor from which to get the data. The cursor is already moved to
     *                   the correct row.
     *
     */

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //Find fields to populate in inflated table
        TextView textNameView = (TextView) view.findViewById(R.id.item_name);
        TextView textBrandView = (TextView) view.findViewById(R.id.item_brand);
        TextView textPriceView = (TextView) view.findViewById(R.id.item_price);
        TextView textQuantityView = (TextView) view.findViewById(R.id.item_quantity);

        //find the columns of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int brandColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_BRAND);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMUN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);

        //Read the item attributes from the Cursor for the current item
        String itemName = cursor.getString(nameColumnIndex);
        String itemBrand = cursor.getString(brandColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        String itemQuantity = cursor.getString(quantityColumnIndex);

        //Populate fields with extracted properties
        textNameView.setText(itemName);
        textBrandView.setText(itemBrand);
        textPriceView.setText(itemPrice);
        textQuantityView.setText(itemQuantity);
    }
}
