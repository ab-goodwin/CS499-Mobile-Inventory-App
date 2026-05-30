// InventoryAdapter.java
package com.example.cs360project2;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

public class InventoryAdapter extends BaseAdapter {

    // uses a cursor to read inventory data from database
    // displays them in gridview on inventory screen
    private Context context;
    private Cursor cursor;


    // stores context and cursor for displaying inventory data
    public InventoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    // tells gridview how many items to display based
    // on number of total records in inventory database
    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        cursor.moveToPosition(position);
        return cursor;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // creates a grid item view for each record in the inventory database
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        cursor.moveToPosition(position);

        // reuses existing view if available
        // inflates new view if no reusable view is available
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        TextView itemName = convertView.findViewById(R.id.itemName);
        TextView itemQuantity = convertView.findViewById(R.id.itemQuantity);
        
        // pulls item name and quantity from cursor position and displays them in the grid item view
        itemName.setText(cursor.getString(cursor.getColumnIndexOrThrow("itemName")));
        itemQuantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))));

        return convertView;
    }
}