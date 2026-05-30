package com.example.cs360project2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private SQLiteDatabase database;
    private GridView gridView;
    private Button addDataButton;
    private EditText itemName, itemQuantity;
    private View dataEntryForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        // Initialize database and UI components
        database = openOrCreateDatabase("AppDB", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS Inventory(itemName TEXT, quantity INTEGER);");

        gridView = findViewById(R.id.gridView);
        addDataButton = findViewById(R.id.addDataButton);
        dataEntryForm = findViewById(R.id.dataEntryForm);
        itemName = findViewById(R.id.itemName);
        itemQuantity = findViewById(R.id.itemQuantity);

        // Buttons for additional inventory actions
        findViewById(R.id.increaseQuantityButton).setOnClickListener(view -> modifyQuantity(true));
        findViewById(R.id.decreaseQuantityButton).setOnClickListener(view -> modifyQuantity(false));
        findViewById(R.id.deleteItemButton).setOnClickListener(view -> deleteItem());

        // Add data button and save action
        addDataButton.setOnClickListener(view -> toggleFormVisibility());
        findViewById(R.id.saveDataButton).setOnClickListener(view -> saveData());

        // Check and request SMS permissions
        checkSmsPermission();

        // Load inventory data
        loadInventory();
    }

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
                Toast.makeText(this, "SMS permission denied. SMS notifications won't work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

    private void saveData() {
        String name = itemName.getText().toString().trim();
        String quantityStr = itemQuantity.getText().toString().trim();

        if (name.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            database.execSQL("INSERT INTO Inventory(itemName, quantity) VALUES(?, ?);", new Object[]{name, quantity});
            Toast.makeText(this, "Item added to inventory!", Toast.LENGTH_SHORT).show();
            itemName.setText("");
            itemQuantity.setText("");
            dataEntryForm.setVisibility(View.GONE);

            // Trigger SMS notification
            sendSmsNotification("+1234567890", "New item added: " + name + " (Qty: " + quantity + ")");

            loadInventory();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
        }
    }

    private void modifyQuantity(boolean increase) {
        String name = itemName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter an item name", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = database.rawQuery("SELECT quantity FROM Inventory WHERE itemName = ?", new String[]{name});
        if (cursor.moveToFirst()) {
            int quantity = cursor.getInt(0);
            quantity = increase ? quantity + 1 : Math.max(0, quantity - 1);

            database.execSQL("UPDATE Inventory SET quantity = ? WHERE itemName = ?", new Object[]{quantity, name});
            Toast.makeText(this, "Quantity updated successfully", Toast.LENGTH_SHORT).show();
            loadInventory();
        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void deleteItem() {
        String name = itemName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter an item name", Toast.LENGTH_SHORT).show();
            return;
        }

        int rowsDeleted = database.delete("Inventory", "itemName = ?", new String[]{name});
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
            loadInventory();
        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadInventory() {
        Cursor cursor = database.rawQuery("SELECT * FROM Inventory", null);
        InventoryAdapter adapter = new InventoryAdapter(this, cursor);
        gridView.setAdapter(adapter);
    }
}