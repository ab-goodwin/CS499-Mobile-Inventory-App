package com.example.cs360project2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;

    // DatabaseHelper replaces SQLite database usage in MainActivity
    private DatabaseHelper databaseHelper;
    private GridView gridView;
    private EditText itemName, itemQuantity;
    private View dataEntryForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        // initializes DatabaseHelper
        // database setup and table creation
        // handled outside of MainActivity
        databaseHelper = new DatabaseHelper(this);

        // connects inventory screen elements to variables
        gridView = findViewById(R.id.gridView);
        Button addDataButton = findViewById(R.id.addDataButton);
        dataEntryForm = findViewById(R.id.dataEntryForm);
        itemName = findViewById(R.id.itemName);
        itemQuantity = findViewById(R.id.itemQuantity);

        // connects inventory management buttons to their
        // corresponding methods
        findViewById(R.id.increaseQuantityButton).setOnClickListener(view -> modifyQuantity(true));
        findViewById(R.id.decreaseQuantityButton).setOnClickListener(view -> modifyQuantity(false));
        findViewById(R.id.deleteItemButton).setOnClickListener(view -> deleteItem());

        // button setup for new items
        addDataButton.setOnClickListener(view -> toggleFormVisibility());
        findViewById(R.id.saveDataButton).setOnClickListener(view -> saveData());

        // Requests SMS permission for inventory notifications
        checkSmsPermission();

        // Loads inventory data into the grid view
        loadInventory();
    }

    // checks for sms permission for inventory notifications
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

    // sends an SMS notification when inventory changes occur
    // hardcoded to specific phone number used for testing
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

            // inventory insert is now handled by DatabaseHelper
            boolean itemAdded = databaseHelper.addInventoryItem(name, quantity);

            if (!itemAdded) {
                Toast.makeText(this, "Item could not be added", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Item added to inventory!", Toast.LENGTH_SHORT).show();
            itemName.setText("");
            itemQuantity.setText("");
            dataEntryForm.setVisibility(View.GONE);

            // sends sms notification after and item is added
            // Future Improvement: only send for low stock
            sendSmsNotification("+1234567890", "New item added: " + name + " (Qty: " + quantity + ")");

            loadInventory();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
        }
    }

    // Updates existing inventory item quantity
    // Boolean value determines whether quantity is decreased or increased
    private void modifyQuantity(boolean increase) {
        String name = itemName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter an item name", Toast.LENGTH_SHORT).show();
            return;
        }

        // now retrieves current quantity from DatabaseHelper
        // instead of writing a raw query in MainActivity
        Integer quantity = databaseHelper.getItemQuantity(name);

        if (quantity != null) {
            // increases or decreases quantity based on boolean
            // math.max keeps quantity from dropping below 0
            int updatedQuantity = increase ? quantity + 1 : Math.max(0, quantity - 1);

            // quantity update is now handled by DatabaseHelper
            boolean quantityUpdated = databaseHelper.updateInventoryQuantity(name, updatedQuantity);

            if (quantityUpdated) {
                Toast.makeText(this, "Quantity updated successfully", Toast.LENGTH_SHORT).show();
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

        // delete is now handled by DatabaseHelper
        boolean itemDeleted = databaseHelper.deleteInventoryItem(name);

        if (itemDeleted) {
            Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
            loadInventory();
        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
        }
    }

    // loads inventory records through DatabaseHelper
    // sends to adapter for display
    private void loadInventory() {

        // retrieves all inventory records from databaseHelper
        Cursor cursor = databaseHelper.getAllInventoryItems();
        InventoryAdapter adapter = new InventoryAdapter(this, cursor);
        gridView.setAdapter(adapter);
    }
}