package com.example.cs360project2;

public class InventoryItem {

    private int itemId;
    private String itemName;
    private int quantity;
    private String category;
    private int lowStockLimit;
    private String dateAdded;
    private String lastUpdated;

    public InventoryItem(int itemId, String itemName, int quantity, String category,
                         int lowStockLimit, String dateAdded, String lastUpdated) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.category = category;
        this.lowStockLimit = lowStockLimit;
        this.dateAdded = dateAdded;
        this.lastUpdated = lastUpdated;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public int getLowStockLimit() {
        return lowStockLimit;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLowStockLimit(int lowStockLimit) {
        this.lowStockLimit = lowStockLimit;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}