package com.example.stuart.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.stuart.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Display lists of items that were entered and stored in the app.
 */

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Adapter for the ListView */
    ItemCursorAdapter mItemCursorAdapter;

    /** Identifier for the item data loader */
    private static final int ITEM_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cricket Store Inventory");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //Locate listview id to link to the adapter
        ListView itemlistView = (ListView) findViewById(R.id.list);


        //initialise ItemCursorAdapter and set this to accept the listview
        mItemCursorAdapter = new ItemCursorAdapter(this, null);
        itemlistView.setAdapter(mItemCursorAdapter);

        //setup onlickListener
        itemlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Create a new intent to go to {@Link EditorActivity}
                Intent intent = new Intent (CatalogActivity.this, EditorActivity.class);

                /**
                 * Form the content URI that represents the specific item that was clicked on,
                 * by appending the "id" (passed as input to the method onto the
                 * {@Link InventoryEntry#CONTENT_URI}.
                 * For example, the URI would be "content://com.example.stuart.inventoryapp.cricket/cricket/2"
                 * if the item with ID 2 was clicker on.
                 */
                Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                //Set the URI on the the data field of the intent
                intent.setData(currentItemUri);

                //Launch the {@link EditorActivity} to display the data for the current item.
                startActivity(intent);
            }
        });

        //start the loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    /**
     * Helper method to delete all items in the database.
     */
    private void deleteAllItems(){
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + "rows deleted from cricket database");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options from the res/menu/menu_catalog.xml file
        //This adds menu to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            //Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_data:
                deleteAllItems();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that specifies which columns from the database
        //You will actually use after this query.
        String [] projection = {InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_BRAND,
                InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryEntry.COLUMUN_ITEM_PRICE,
                };

        return new CursorLoader(this, InventoryEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Update {@link itemCursorAdapter} with this new cursor containing updated item data
        mItemCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mItemCursorAdapter.swapCursor(null);
    }
}




