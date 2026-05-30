package com.example.cs360project2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// added imports for the algorithms and data structures enhancement
// ArrayList stores inventory records as objects
// collections supports list sorting
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;

    // added a hardcoded low stock limit for filtering inventory items
    // Possible Improvement: let the user change the value of low_stock_limit in application
    private static final int LOW_STOCK_LIMIT = 5;

    // DatabaseHelper replaces direct SQLite database usage in MainActivity
    private DatabaseHelper databaseHelper;

    private GridView gridView;
    private EditText itemName, itemQuantity;

    // added searchField for searching inventory by item name
    private EditText searchField;

    // added dropdown and switch for algorithms enhancement
    // sortSpinner controls sorting, lowStockSwitch toggles between all items and low-stock items
    private Spinner sortSpinner;
    private Switch lowStockSwitch;

    private View dataEntryForm;

    // added ArrayLists as part of the algorithms and data structures enhancement
    // inventoryItems stores the full inventory list from the database
    // displayedItems stores the current list shown after search, sort, or filter
    private ArrayList<InventoryItem> inventoryItems = new ArrayList<>();
    private ArrayList<InventoryItem> displayedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        // initializes DatabaseHelper
        // database setup and table creation are handled outside of MainActivity
        databaseHelper = new DatabaseHelper(this);

        // connects inventory screen elements to variables
        gridView = findViewById(R.id.gridView);
        Button addDataButton = findViewById(R.id.addDataButton);
        dataEntryForm = findViewById(R.id.dataEntryForm);
        itemName = findViewById(R.id.itemName);
        itemQuantity = findViewById(R.id.itemQuantity);

        // gridview item selection
        // the user selects an inventory item, and name and quantity autofill
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            InventoryItem selectedItem = displayedItems.get(position);

            itemName.setText(selectedItem.getItemName());
            itemQuantity.setText(String.valueOf(selectedItem.getQuantity()));

            dataEntryForm.setVisibility(View.VISIBLE);

        });

        // added search field, sort dropdown, and low stock switch
        searchField = findViewById(R.id.searchField);
        sortSpinner = findViewById(R.id.sortSpinner);
        lowStockSwitch = findViewById(R.id.lowStockSwitch);

        // connects inventory management buttons to their corresponding methods
        findViewById(R.id.increaseQuantityButton).setOnClickListener(view -> modifyQuantity(true));
        findViewById(R.id.decreaseQuantityButton).setOnClickListener(view -> modifyQuantity(false));
        findViewById(R.id.deleteItemButton).setOnClickListener(view -> deleteItem());

        // button setup for adding new inventory items
        addDataButton.setOnClickListener(view -> toggleFormVisibility());
        findViewById(R.id.saveDataButton).setOnClickListener(view -> saveData());

        // search button runs the search algorithm
        findViewById(R.id.searchButton).setOnClickListener(view -> searchInventory());

        // added dropdown menu to select between filters
        // This replaces the separate Sort by Name and Sort by Quantity buttons
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Sort by Name", "Sort by Quantity ▲", "Sort by Quantity ▼"}
        );

        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        // runs the selected sorting algorithm when the user chooses an option
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    sortInventoryByName();
                }
                if (position == 1) {
                    sortInventoryByQuantityReverse();
                }
                if (position == 2) {
                    sortInventoryByQuantity();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        // added switch to toggle between all inventory items and low stock items
        lowStockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                filterLowStock();
            } else {
                showAllInventory();
            }
        });

        // Requests SMS permission for inventory notifications
        checkSmsPermission();

        // loads inventory data into the grid view
        loadInventory();
    }

    // checks for SMS permission for inventory notifications
    private void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied. SMS notifications will not work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Sends an SMS notification when inventory changes occur
    // Hardcoded to specific phone number used for testing
    // Future improvement: change to have phone number based on user input
    private void sendSmsNotification(String phoneNumber, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "SMS permission not granted. Unable to send SMS.", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFormVisibility() {
        if (dataEntryForm.getVisibility() == View.GONE) {
            dataEntryForm.setVisibility(View.VISIBLE);
        } else {
            dataEntryForm.setVisibility(View.GONE);
        }
    }

    // saves new inventory item after validating user input
    private void saveData() {
        String name = itemName.getText().toString().trim();
        String quantityStr = itemQuantity.getText().toString().trim();

        // basic validation to ensure all fields are occupied
        if (name.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);

            // prevents negative quantities from being saved
            if (quantity < 0) {
                Toast.makeText(this, "Quantity cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }

            // inventory insert is handled by DatabaseHelper
            boolean itemAdded = databaseHelper.addInventoryItem(name, quantity);

            if (!itemAdded) {
                Toast.makeText(this, "Item could not be added", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Item added to inventory!", Toast.LENGTH_SHORT).show();
            itemName.setText("");
            itemQuantity.setText("");
            dataEntryForm.setVisibility(View.GONE);

            // Sends SMS notification after an item is added
            // Future improvement: only send for low stock
            sendSmsNotification("+1234567890", "New item added: " + name + " (Qty: " + quantity + ")");

            // reloads the full list after adding a new item
            loadInventory();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
        }
    }

    // updates existing inventory item quantity
    // boolean value determines whether quantity is decreased or increased
    private void modifyQuantity(boolean increase) {
        String name = itemName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter an item name", Toast.LENGTH_SHORT).show();
            return;
        }

        // retrieves current quantity from DatabaseHelper
        Integer quantity = databaseHelper.getItemQuantity(name);

        if (quantity != null) {
            // increases / decreases quantity based on boolean
            // math.max keeps quantity from dropping below 0
            int updatedQuantity = increase ? quantity + 1 : Math.max(0, quantity - 1);

            // quantity update is handled by DatabaseHelper
            boolean quantityUpdated = databaseHelper.updateInventoryQuantity(name, updatedQuantity);

            if (quantityUpdated) {
                loadInventory();
            } else {
                Toast.makeText(this, "Quantity could not be updated", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
        }
    }

    // deletes inventory item using DatabaseHelper
    private void deleteItem() {
        String name = itemName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter an item name", Toast.LENGTH_SHORT).show();
            return;
        }

        // delete handled by DatabaseHelper
        boolean itemDeleted = databaseHelper.deleteInventoryItem(name);

        if (itemDeleted) {
            Toast.makeText(this, "Item deleted!", Toast.LENGTH_SHORT).show();
            loadInventory();
        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Changed for algorithms and data structures enhancement
    // loads inventory records into an ArrayList instead of displaying with raw Cursor directly
    private void loadInventory() {
        inventoryItems = databaseHelper.getInventoryItemList();

        // displayedItems starts as copy of the full list
        displayedItems = new ArrayList<>(inventoryItems);

        // keeps low stock switch consistent when refreshing
        if (lowStockSwitch != null && lowStockSwitch.isChecked()) {
            filterLowStock();
        } else {
            refreshInventoryDisplay();
        }
    }

    // added helper method to refresh gridview
    // prevents repeated adapter setup for search sort and filter
    private void refreshInventoryDisplay() {
        InventoryAdapter adapter = new InventoryAdapter(this, displayedItems);
        gridView.setAdapter(adapter);
    }

    // searching
    // loops through inventory list for matching item name
    private void searchInventory() {
        String searchText = searchField.getText().toString().trim().toLowerCase();

        displayedItems = new ArrayList<>();

        if (searchText.isEmpty()) {
            displayedItems.addAll(inventoryItems);
        } else {
            for (InventoryItem item : inventoryItems) {
                if (item.getItemName().toLowerCase().contains(searchText)) {
                    displayedItems.add(item);
                }
            }
        }

        // If low stock switch is on - only display low stock items
        if (lowStockSwitch.isChecked()) {
            ArrayList<InventoryItem> lowStockSearchResults = new ArrayList<>();

            for (InventoryItem item : displayedItems) {
                if (item.getQuantity() <= LOW_STOCK_LIMIT) {
                    lowStockSearchResults.add(item);
                }
            }

            displayedItems = lowStockSearchResults;
        }

        refreshInventoryDisplay();
    }

    // sort alphabetically
    // sorts the displayed list by name
    private void sortInventoryByName() {
        Collections.sort(displayedItems, (firstItem, secondItem) ->
                firstItem.getItemName().compareToIgnoreCase(secondItem.getItemName())
        );

        refreshInventoryDisplay();
    }

    // sort by quantity
    // sorts the displayed list by quantity (low to high)
    private void sortInventoryByQuantity() {
        Collections.sort(displayedItems, (firstItem, secondItem) ->
                Integer.compare(firstItem.getQuantity(), secondItem.getQuantity())
        );

        refreshInventoryDisplay();
    }

    // sort by quantity (reverse)
    // sorts displayed list by quantity (high to low)
    private void sortInventoryByQuantityReverse() {
        Collections.sort(displayedItems, (firstItem, secondItem) ->
                Integer.compare(secondItem.getQuantity(), firstItem.getQuantity())
        );
        refreshInventoryDisplay();
    }

    // low stock filter
    // loops through inventory and only displays items at or below low stock limit
    private void filterLowStock() {
        displayedItems = new ArrayList<>();

        for (InventoryItem item : inventoryItems) {
            if (item.getQuantity() <= LOW_STOCK_LIMIT) {
                displayedItems.add(item);
            }
        }

        refreshInventoryDisplay();
    }

    // Shows all inventory items when the low-stock switch is turned off
    private void showAllInventory() {
        displayedItems = new ArrayList<>(inventoryItems);
        refreshInventoryDisplay();
    }
}