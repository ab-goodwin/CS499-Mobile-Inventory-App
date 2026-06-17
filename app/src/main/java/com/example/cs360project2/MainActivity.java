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

    // DatabaseHelper replaces direct SQLite database usage in MainActivity
    private DatabaseHelper databaseHelper;

    private GridView gridView;

    // added searchField for searching inventory by item name
    private EditText searchField;

    // added dropdown and switch for algorithms enhancement
    // sortSpinner controls sorting, lowStockSwitch toggles between all items and low-stock items
    private Spinner sortSpinner;
    private Switch lowStockSwitch;

    // separate forms for adding new items and updating selected items
    private View addItemForm, updateItemForm;

    // fields used only when adding a new inventory item
    private EditText addItemName, addQuantity, addCategory, addLowStockLimit;

    // fields used only when updating a selected inventory item
    private EditText updateItemName, updateQuantity, updateCategory, updateLowStockLimit;

    // stores the selected database item ID for update and delete operations
    private int selectedItemId = -1;

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

        // connects separate add and update forms to MainActivity
        addItemForm = findViewById(R.id.addItemForm);
        updateItemForm = findViewById(R.id.updateItemForm);

        // connects fields for adding a new item
        addItemName = findViewById(R.id.addItemName);
        addQuantity = findViewById(R.id.addQuantity);
        addCategory = findViewById(R.id.addCategory);
        addLowStockLimit = findViewById(R.id.addLowStockLimit);

        // connects fields for updating a selected item
        updateItemName = findViewById(R.id.updateItemName);
        updateQuantity = findViewById(R.id.updateQuantity);
        updateCategory = findViewById(R.id.updateCategory);
        updateLowStockLimit = findViewById(R.id.updateLowStockLimit);

        // New button opens the add form
        // Update button opens the update form for the selected item
        Button newItemButton = findViewById(R.id.newItemButton);
        Button updateDataButton = findViewById(R.id.updateDataButton);

        newItemButton.setOnClickListener(view -> toggleAddItemForm());
        updateDataButton.setOnClickListener(view -> toggleUpdateItemForm());

        // Add button creates a new inventory item
        // Update button saves changes to the selected item
        findViewById(R.id.addItemSubmitButton).setOnClickListener(view -> addNewItem());
        findViewById(R.id.updateItemSubmitButton).setOnClickListener(view -> updateSelectedItem());

        // +1 and -1 buttons update the quantity field before saving
        findViewById(R.id.increaseQuantityButton).setOnClickListener(view -> adjustUpdateQuantity(1));
        findViewById(R.id.decreaseQuantityButton).setOnClickListener(view -> adjustUpdateQuantity(-1));

        // delete button removes the selected item by item ID
        findViewById(R.id.deleteItemButton).setOnClickListener(view -> deleteSelectedItem());

        // gridview item selection
        // stores selected item ID and autofills update fields
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            InventoryItem selectedItem = displayedItems.get(position);

            // selectedItemId is used so updates and deletes happen by primary key
            selectedItemId = selectedItem.getItemId();

            updateItemName.setText(selectedItem.getItemName());
            updateQuantity.setText(String.valueOf(selectedItem.getQuantity()));
            updateCategory.setText(selectedItem.getCategory());
            updateLowStockLimit.setText(String.valueOf(selectedItem.getLowStockLimit()));

            addItemForm.setVisibility(View.GONE);
            updateItemForm.setVisibility(View.VISIBLE);

        });

        // added search field, sort dropdown, and low stock switch
        searchField = findViewById(R.id.searchField);
        sortSpinner = findViewById(R.id.sortSpinner);
        lowStockSwitch = findViewById(R.id.lowStockSwitch);

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
                    sortInventoryByQuantity();
                }
                if (position == 2) {
                    sortInventoryByQuantityReverse();
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

    // opens or closes the add item form
    // closes update form so only one menu is open
    private void toggleAddItemForm() {
        if (addItemForm.getVisibility() == View.VISIBLE) {
            addItemForm.setVisibility(View.GONE);
        } else {
            updateItemForm.setVisibility(View.GONE);
            addItemForm.setVisibility(View.VISIBLE);
        }
    }

    // opens or closes the update item form
    // requires an item to be selected before updating
    private void toggleUpdateItemForm() {
        if (updateItemForm.getVisibility() == View.VISIBLE) {
            updateItemForm.setVisibility(View.GONE);
        } else {
            addItemForm.setVisibility(View.GONE);

            if (selectedItemId == -1) {
                Toast.makeText(this, "Please select an item to update", Toast.LENGTH_SHORT).show();
                return;
            }

            updateItemForm.setVisibility(View.VISIBLE);
        }
    }

    // adds a new inventory item after validating all input fields
    private void addNewItem() {
        String name = addItemName.getText().toString().trim();
        String quantityStr = addQuantity.getText().toString().trim();
        String category = addCategory.getText().toString().trim();
        String lowStockStr = addLowStockLimit.getText().toString().trim();

        // validates that all new item fields are filled
        if (name.isEmpty() || quantityStr.isEmpty() || category.isEmpty() || lowStockStr.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            int lowStockLimit = Integer.parseInt(lowStockStr);

            // prevents negative quantity or low-stock values
            if (quantity < 0 || lowStockLimit < 0) {
                Toast.makeText(this, "Quantity and low stock limit cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }

            // checks for duplicate item names before inserting
            if (databaseHelper.itemNameExists(name)) {
                Toast.makeText(this, "Item name already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            // adds item using expanded database fields
            boolean itemAdded = databaseHelper.addInventoryItem(name, quantity, category, lowStockLimit);

            if (!itemAdded) {
                Toast.makeText(this, "Item could not be added", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Item added to inventory!", Toast.LENGTH_SHORT).show();

            addItemName.setText("");
            addQuantity.setText("");
            addCategory.setText("");
            addLowStockLimit.setText("");

            addItemForm.setVisibility(View.GONE);

            // Sends SMS notification after an item is added
            // Future improvement: only send for low stock
            sendSmsNotification("+1234567890", "New item added: " + name + " (Qty: " + quantity + ")");

            // reloads the full list after adding a new item
            loadInventory();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    // updates the selected inventory item by item ID
    private void updateSelectedItem() {
        if (selectedItemId == -1) {
            Toast.makeText(this, "Please select an item to update", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = updateItemName.getText().toString().trim();
        String quantityStr = updateQuantity.getText().toString().trim();
        String category = updateCategory.getText().toString().trim();
        String lowStockStr = updateLowStockLimit.getText().toString().trim();

        // validates all update fields before saving changes
        if (name.isEmpty() || quantityStr.isEmpty() || category.isEmpty() || lowStockStr.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            int lowStockLimit = Integer.parseInt(lowStockStr);

            // prevents negative quantity or low-stock values
            if (quantity < 0 || lowStockLimit < 0) {
                Toast.makeText(this, "Quantity and low stock limit cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }

            // updates selected item by primary key instead of item name
            boolean itemUpdated = databaseHelper.updateInventoryItem(
                    selectedItemId,
                    name,
                    quantity,
                    category,
                    lowStockLimit
            );

            if (itemUpdated) {
                Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();

                updateItemForm.setVisibility(View.GONE);
                selectedItemId = -1;

                loadInventory();
            } else {
                Toast.makeText(this, "Item could not be updated. Name may already exist.", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    // adjusts the update quantity field and saves the change to the database
// quantity changes now use item ID instead of item name
private void adjustUpdateQuantity(int change) {
    if (selectedItemId == -1) {
        Toast.makeText(this, "Please select an item first", Toast.LENGTH_SHORT).show();
        return;
    }

    String quantityStr = updateQuantity.getText().toString().trim();

    int quantity = 0;

    if (!quantityStr.isEmpty()) {
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    quantity += change;

    if (quantity < 0) {
        quantity = 0;
    }

    updateQuantity.setText(String.valueOf(quantity));

    boolean quantityUpdated = databaseHelper.updateInventoryQuantityById(selectedItemId, quantity);

    if (quantityUpdated) {
        loadInventory();
    } else {
        Toast.makeText(this, "Quantity could not be updated", Toast.LENGTH_SHORT).show();
    }
}

    // deletes the selected inventory item by item ID
    private void deleteSelectedItem() {
        if (selectedItemId == -1) {
            Toast.makeText(this, "Please select an item to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean itemDeleted = databaseHelper.deleteInventoryItemById(selectedItemId);

        if (itemDeleted) {
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();

            updateItemForm.setVisibility(View.GONE);
            selectedItemId = -1;

            loadInventory();
        } else {
            Toast.makeText(this, "Item could not be deleted", Toast.LENGTH_SHORT).show();
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
    // now searches in both inventory item names and categories
    private void searchInventory() {
        String searchText = searchField.getText().toString().trim().toLowerCase();

        displayedItems = new ArrayList<>();

        if (searchText.isEmpty()) {
            displayedItems.addAll(inventoryItems);
        } else {
            for (InventoryItem item : inventoryItems) {
                if (item.getItemName().toLowerCase().contains(searchText) 
                    || item.getCategory().toLowerCase().contains(searchText)){
                    displayedItems.add(item);
                }
            }
        }

        // uses each item's stored lowStockLimit when filtering search results
        if (lowStockSwitch.isChecked()) {
            ArrayList<InventoryItem> lowStockSearchResults = new ArrayList<>();

            for (InventoryItem item : displayedItems) {
                if (item.getQuantity() <= item.getLowStockLimit()) {
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
    // uses each item's stored lowStockLimit instead of a hardcoded value
    private void filterLowStock() {
        displayedItems = new ArrayList<>();

        for (InventoryItem item : inventoryItems) {
            if (item.getQuantity() <= item.getLowStockLimit()) {
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