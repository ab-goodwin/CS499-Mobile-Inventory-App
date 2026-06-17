# Enhancement Three: Databases

## Artifact Overview

This enhancement is based on my CS360 Mobile Inventory Application. The application was originally created in Android Studio using Java and SQLite. The app allows users to create an account, log in, and manage inventory items through a mobile interface. Once logged in, users can add inventory items, update quantities, delete items, and view the inventory in a GridView.

The original application used a simple database structure that stored item names and quantities. While functional, the database relied on item names for identifying records, which created limitations when updating or deleting inventory items. As the application evolved, the database design needed to support more detailed inventory information and more reliable record management.

## Enhancement Summary

For this enhancement, I focused on improving the database structure and data management within the application. I expanded the inventory table by introducing a unique item ID as the primary key and adding additional fields to store more detailed inventory information.

The main changes include:

* Added a unique item ID as the primary identifier for inventory records
* Updated inventory updates and deletions to use item IDs instead of item names
* Added category field for inventory organization
* Added low-stock limit field for item-specific inventory thresholds
* Added date added field
* Added last updated field
* Added validation to prevent duplicate item names
* Added validation for empty fields and negative values
* Updated low-stock filtering to use item-specific low-stock limits
* Separated item creation and item update workflows into separate forms

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
* 'grid_item.xml'

## Skills Demonstrated

This enhancement demonstrates database design, data management, validation, and application maintenance. The largest improvement was redesigning the inventory database to use a unique item ID as the primary key rather than relying on item names for record identification.

The enhancement also expanded the database structure by adding category, low-stock limit, date added, and last updated fields. These additions allow the application to store more meaningful inventory information and support more advanced inventory management features.

By implementing validation rules and using item IDs for updates and deletions, the application became more reliable and less vulnerable to data integrity issues caused by duplicate names or invalid user input.

## Course Outcomes Addressed

This enhancement supports the following course outcomes:

* **Outcome 2:** Communicating technical decisions through documentation, code comments, and written explanation
* **Outcome 3:** Evaluating and improving an existing database design to better manage and organize data
* **Outcome 4:** Using Android Studio, Java, SQLite, and database development techniques to enhance a working application
* **Outcome 5:** Improving data integrity and reducing risks through validation, unique identifiers, and reliable database operations
