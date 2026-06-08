# Enhancement Two: Algorithms and Data Structures

## Artifact Overview

This enhancement is based on my CS360 Mobile Inventory Application. The application was originally created in Android Studio using Java and SQLite. The app allows users to create an account, log in, and manage inventory items through a mobile interface.

The original version displayed inventory records from the database, but the inventory list was very basic. Users could view items, but there was no way to search, sort, or filter the list. As more items were added, the app would become harder to use because the user would have to scroll through the list or type an item name exactly to make changes.

## Enhancement Summary

For this enhancement, I focused on improving how the app organizes and processes inventory data. I added `InventoryItem.java` so inventory records could be handled as objects. I also updated the app to use `ArrayList<InventoryItem>` instead of only relying on a raw database cursor for displaying inventory records.

The main changes include:

* Added `InventoryItem.java`
* Updated `DatabaseHelper.java` to return inventory records as an `ArrayList`
* Updated `InventoryAdapter.java` to display inventory objects
* Updated `MainActivity.java` to support search, sorting, and filtering
* Added search by item name
* Added sorting by item name
* Added sorting by quantity in both directions
* Added low-stock filtering
* Added a dropdown menu for sorting options
* Added a low-stock switch to toggle between all items and low-stock items
* Added item selection so tapping an item autofills the update fields

## Files Included

### Original Code

* `LoginActivity.java`
* `MainActivity.java`
* `InventoryAdapter.java`

### Enhanced Code

* `LoginActivity.java`
* `MainActivity.java`
* `InventoryAdapter.java`
* `DatabaseHelper.java`
* `InventoryItem.java`
* `activity_data_display.xml`

## Skills Demonstrated

This enhancement demonstrates algorithms and data structures by using lists to manage inventory data before displaying it to the user. The app now uses one list to store the full inventory and another list to store the currently displayed results after searching, sorting, or filtering.

The search feature loops through the inventory list and finds matching items. The sorting features organize the list by name or quantity. The low-stock filter checks item quantities and displays only items that meet the low-stock condition. These changes made the inventory screen more useful and easier to manage.

## Course Outcomes Addressed

This enhancement supports the following course outcomes:

* **Outcome 2:** Communicating technical decisions through code comments and written explanation
* **Outcome 3:** Applying algorithms and data structures to solve a practical inventory management problem
* **Outcome 4:** Using Android Studio, Java, SQLite, ArrayLists, adapters, and interface controls to improve a working application


The enhancement kept the original app functionality while making the inventory list more organized and easier to use.
