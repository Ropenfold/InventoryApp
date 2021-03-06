package com.example.stuart.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stuart.inventoryapp.data.InventoryContract;
import com.example.stuart.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.stuart.inventoryapp.data.InventoryDBHelper;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

        /** Identifier for the pet data loader */
        private static final int EXISTING_ITEM_LOADER = 0;

        // Content URI for the existing item (null if it's a new item)
        private Uri mCurrentItemUri;

        /** EditText field to enter the item's name */
        private EditText mItemEditText;

        /** EditText field to enter the brand name */
        private EditText mBrandEditText;

        /** EditText field to enter a quantity */
        private EditText mQuantityEditText;

        /** EditText for item's price */
        private EditText mItemPriceText;

        /** Button for increment quantity */
        private Button mQuantityIncrement;

        /** Button for decrement quantity */
        private Button mQuantityDecrement;

        /** Button for increment size */
        private Button mPriceIncrement;

        /** Button for decrement quantity */
        private Button mPriceDecrement;

    //check to see if item has changed
    private boolean mItemHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are
    // modifying the view, we then change the mItemHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Examine the intent that was used to launch this Activity,
        // in order to figure out if we're creating an item or editing an existing one.

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        /**
         * If the intent does not contain a item content URI, then we know
         * we are creating a new item
         */


         if(mCurrentItemUri == null) {
            //This is a new item, so the app bar will say "Add a new item"
            setTitle(R.string.editor_activity_title_new_item);
             //Invalidate the options menu, so the delete menu option can be hidden.
             //It stops you deleting an item that has not been created yet
             invalidateOptionsMenu();

        } else {
            setTitle(R.string.editor_activity_title_edit_an_item);

             // Initialize a loader to read the item data from the database
             // and display the current values in the editor
             getLoaderManager().initLoader(EXISTING_ITEM_LOADER,  null, this);

         }

        //Find all relevant views that we will need to read user input from
        mItemEditText = (EditText) findViewById(R.id.edit_item_name);
        mBrandEditText = (EditText) findViewById(R.id.edit_item_brand);
        mItemPriceText = (EditText) findViewById(R.id.edit_item_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_item_quantity);
        mQuantityIncrement = (Button) findViewById(R.id.quantity_increment);
        mQuantityDecrement = (Button) findViewById(R.id.quantity_decrement);
        mPriceIncrement = findViewById(R.id.price_increment);
        mPriceDecrement = findViewById(R.id.price_decrement);


        mItemEditText.setOnTouchListener(mTouchListener);
        mBrandEditText.setOnTouchListener(mTouchListener);
        mItemPriceText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);

        mQuantityIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantityIncrement();
                mItemHasChanged = true;
            }
        });

        mQuantityDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantityDecrement();
                mItemHasChanged = true;
            }
        });

        mPriceIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceIncrement();
                mItemHasChanged = true;
            }
        });

        mPriceDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceDecrement();
                mItemHasChanged = true;
            }
        });



    }


    private void quantityDecrement() {
        String previousValueString = mQuantityEditText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            mQuantityEditText.setText(String.valueOf(previousValue - 1));
        }
    }

    private void quantityIncrement() {
        String previousValueString = mQuantityEditText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        mQuantityEditText.setText(String.valueOf(previousValue + 1));
    }

    private void priceDecrement() {
        String previousValueString = mItemPriceText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            mItemPriceText.setText(String.valueOf(previousValue - 1));
        }
    }

    private void priceIncrement() {
        String previousValueString = mItemPriceText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        mItemPriceText.setText(String.valueOf(previousValue + 1));
    }



    private void saveItem () {

        //Get information from the seperate fields
        String itemString = mItemEditText.getText().toString().trim();
        String brandString = mBrandEditText.getText().toString().trim();
        String priceString = mItemPriceText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();


        //check if this is supposed to be a new item
        //Check if all the fields in the editor are blank
        if (mCurrentItemUri == null &&
        TextUtils.isEmpty(itemString) && TextUtils.isEmpty(brandString) && TextUtils.isEmpty(priceString)
                && TextUtils.isEmpty(quantityString)){

            /**
            *Since no fields were modified, we can return early without creating a new item
            * No need to create contentValues and no need to do any ContentProvider Operations.
            */
            return;
        }

        //Add values to ContentValues object
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_ITEM_NAME, itemString);
        values.put(InventoryEntry.COLUMN_ITEM_BRAND, brandString);
        values.put(InventoryEntry.COLUMUN_ITEM_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, quantityString);

        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if(!TextUtils.isEmpty(priceString)){
            price = Integer.parseInt(priceString);
        }

        values.put(InventoryEntry.COLUMUN_ITEM_PRICE, price);

        // If the Quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)){
            quantity = Integer.parseInt(quantityString);
        }

        values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, quantity);

        // Determine if this is a new or existing item by chekcing if mCurrentItemUri is null or not
        if (mCurrentItemUri == null){
            // This is NEW item, so insert a new item into the provider,
            // returning the content URI for the new item.

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            //Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {

                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();

                } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING item, so update the item with content URI: mCurrentItemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
        int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0){
        // If no rows were effected, then there was an error with the update.
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                    Toast.LENGTH_SHORT).show();

        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                    Toast.LENGTH_SHORT).show();
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            //Respond to a click on the "Save" menu option
            case R.id.action_save:
                //add item to the database
                saveItem();
                //finish the activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog to deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //User clicked "Discard button", navigate to parent activity
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                    //Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
            }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "keep editing" button, so dismiss the dialog
                // aqnd continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this item.
     */
    private void showDeleteConfirmationDialog () {
    // Create an AlertDialog.Builder and set the message, and click listeners
    // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                    if (dialog != null) {
                        dialog.dismiss();
                    }

            }
        });

                //Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
    }

    //Perform the deletion of the item in the database.

    private void deleteItem () {
        // Only perform the delete if this is an existing item.
            if (mCurrentItemUri != null) {
                // Call the ContentResolver to delete the item and the given content URI.
                //Pass in null for the selection and selection args because the mCurrentItemUri
                //content URI already identifies the item that we need.
                int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

                // Show a toast message depending on whether or not the delete was successful.
                        if (rowsDeleted == 0) {
                        // If no rows were deleted, then there was an error with the delete.
                        Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                                Toast.LENGTH_SHORT).show();
                        } else {
                            // Otherwise, the delete was successful and we can display a toast.
                            Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                                    Toast.LENGTH_SHORT).show();
                        }
            }
            // Close the activity
            finish();
    }

    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button pressed
        if(!mItemHasChanged){
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should
        // be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };

        //Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args ) {

        // Since the editor shows all the item attributes, define a projection
        // that contains all columns from the item table
        String [] projection = {
            InventoryEntry._ID,
            InventoryEntry.COLUMN_ITEM_NAME,
            InventoryEntry.COLUMN_ITEM_BRAND,
                InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryEntry.COLUMUN_ITEM_PRICE,

        };

        return new CursorLoader(this,
                                        mCurrentItemUri,
                                        projection,
                                        null,
                                        null,
                                        null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading
        // data from it
        // This should be the only row in the cursor
        if(cursor.moveToFirst()){
            // Find the columns of item attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int brandColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_BRAND);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMUN_ITEM_PRICE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String brand = cursor.getString(brandColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            // Update the views on the screen with the values form the database
            mItemEditText.setText(name);
            mBrandEditText.setText(brand);
            mItemPriceText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mItemEditText.setText("");
        mBrandEditText.setText("");
        mQuantityEditText.setText("");
        mItemPriceText.setText("");
    }


}
