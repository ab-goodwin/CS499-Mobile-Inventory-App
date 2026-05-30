package com.example.cs360project2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


// DatabaseHelper focuses on database setup and operation
// Keeps SQLite logic out of LoginActivity and MainActivity files
public class DatabaseHelper extends SQLiteOpenHelper {

    // database name and version are saved as variables for easy management
    private static final String databaseName = "AppDB";
    private static final int databaseVersion = 1;

    // table names saved to avoid repeating hardcoded strings
    private static final String usersTable = "Users";
    private static final String inventoryTable = "Inventory";

    // column names are defined to make queries easier
    private static final String usernameColumn = "username";
    private static final String passwordColumn = "password";

    // inventory table columns
    private static final String itemNameColumn = "itemName";
    private static final String quantityColumn = "quantity";

    // connects the helper class to the local SQLite database
    public DatabaseHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    // creates the users and inventory tables when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // creates the users table for logins
        String createUsersTable = "CREATE TABLE IF NOT EXISTS " + usersTable + " (" +
                usernameColumn + " TEXT PRIMARY KEY, " +
                passwordColumn + " TEXT);";

        // creates the inventory table for storage
        String createInventoryTable = "CREATE TABLE IF NOT EXISTS " + inventoryTable + " (" +
                itemNameColumn + " TEXT, " +
                quantityColumn + " INTEGER);";

        db.execSQL(createUsersTable);
        db.execSQL(createInventoryTable);
    }

    @Override
    // drops the tables if database version is updated
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + usersTable);
        db.execSQL("DROP TABLE IF EXISTS " + inventoryTable);
        onCreate(db);
    }

    // hashes password before they are stored and compared
    // improves security by not storing passwords as plaintext
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    // creates a new user
    // password is hashed before saving to database
    public boolean createUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(usernameColumn, username);
        values.put(passwordColumn, hashPassword(password));

        long result = db.insert(usersTable, null, values);

        return result != -1;
    }

    // checks login credentials by hashing password entered
    // and comparing to the stored hashed password

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String hashedPassword = hashPassword(password);

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + usersTable +
                        " WHERE " + usernameColumn + " = ? AND " +
                        passwordColumn + " = ?",
                new String[]{username, hashedPassword}
        );

        boolean userExists = cursor.moveToFirst();
        cursor.close();

        return userExists;
    }

    // adds new inventory item through the helper class
    // instead of inserting directly from MainActivity
    public boolean addInventoryItem(String itemName, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(itemNameColumn, itemName);
        values.put(quantityColumn, quantity);

        long result = db.insert(inventoryTable, null, values);

        return result != -1;
    }

    // returns all inventory records so MainActivity can
    // display them in gridview
    public Cursor getAllInventoryItems() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT * FROM " + inventoryTable, null);
    }

    // returns all inventory quantities for specific items and
    // supports increase and decrease quantity buttons
    public Integer getItemQuantity(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + quantityColumn +
                        " FROM " + inventoryTable +
                        " WHERE " + itemNameColumn + " = ?",
                new String[]{itemName}
        );

        Integer quantity = null;

        if (cursor.moveToFirst()) {
            quantity = cursor.getInt(0);
        }

        cursor.close();

        return quantity;
    }

    // updates existing item quantity
    public boolean updateInventoryQuantity(String itemName, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(quantityColumn, quantity);

        int rowsUpdated = db.update(
                inventoryTable,
                values,
                itemNameColumn + " = ?",
                new String[]{itemName}
        );

        return rowsUpdated > 0;
    }

    // delete inventory item by name *TO BE CHANGED*
    public boolean deleteInventoryItem(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(
                inventoryTable,
                itemNameColumn + " = ?",
                new String[]{itemName}
        );

        return rowsDeleted > 0;
    }

    // returns inventory records as an arraylist
    // supports searching, sorting, and filtering logic in MainActivity
    public ArrayList<InventoryItem> getInventoryItemList() {
        ArrayList<InventoryItem> inventoryList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + inventoryTable, null);

        if (cursor.moveToFirst()) {
            do {
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow(itemNameColumn));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(quantityColumn));

                inventoryList.add(new InventoryItem(itemName, quantity));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return inventoryList;
    }
}
