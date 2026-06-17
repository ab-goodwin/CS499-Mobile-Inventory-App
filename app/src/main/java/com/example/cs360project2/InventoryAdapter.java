// InventoryAdapter.java
package com.example.cs360project2;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

// removed inventory cursor. changed to arraylist
import java.util.ArrayList;

public class InventoryAdapter extends BaseAdapter {

    // adapter changed to arraylist instead of cursor
    // supports search, sort, and filter functionality
    private Context context;
    private ArrayList<InventoryItem> inventoryItems;

    public InventoryAdapter(Context context, ArrayList<InventoryItem> inventoryItems) {
        this.context = context;
        this.inventoryItems = inventoryItems;
    }

    // tells gridview how many filtered or sorted items to display
    @Override
    public int getCount() {
        return inventoryItems.size();
    }

    @Override
    public Object getItem(int position) {
        return inventoryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // creates a grid item view for each record in the inventory database
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InventoryItem currentItem = inventoryItems.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        TextView itemName = convertView.findViewById(R.id.itemName);
        TextView itemQuantity = convertView.findViewById(R.id.itemQuantity);
        TextView itemCategory = convertView.findViewById(R.id.itemCategory);
        TextView lowStockLimit = convertView.findViewById(R.id.lowStockLimit);

        itemName.setText(currentItem.getItemName());
        itemQuantity.setText("Qty: " + currentItem.getQuantity());
        itemCategory.setText(currentItem.getCategory());
        lowStockLimit.setText("Low Stock: " + currentItem.getLowStockLimit());

        return convertView;
    }
}