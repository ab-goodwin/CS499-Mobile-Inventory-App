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

    private Context context;
    private Cursor cursor;

    public InventoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        cursor.moveToPosition(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        TextView itemName = convertView.findViewById(R.id.itemName);
        TextView itemQuantity = convertView.findViewById(R.id.itemQuantity);

        itemName.setText(cursor.getString(cursor.getColumnIndexOrThrow("itemName")));
        itemQuantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))));

        return convertView;
    }
}