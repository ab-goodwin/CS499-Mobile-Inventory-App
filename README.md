# CS499 Final Project: Mobile Inventory Application

## Artifact Overview

The artifact for my capstone project is my CS360 Mobile Inventory Application, developed in Android Studio using Java and SQLite. The application provides a mobile inventory management system that allows users to create an account, log in, and manage inventory items through a simple interface. Once authenticated, users can add new items, update quantities, delete items, and view their inventory in a GridView-based layout.

Originally, the application was designed as a functional but minimal inventory system. Over the course of the capstone, it was significantly expanded through three structured enhancements focusing on software design, algorithms and data structures, and database architecture. Each enhancement built on the previous version of the application to improve maintainability, usability, performance, and data integrity.

---

## Project Enhancements Summary

### Enhancement One: Software Design and Engineering

The first enhancement focused on improving the overall software architecture and separating concerns within the application. In the original version, database logic was embedded directly within activity files, which made the application harder to maintain and extend.

To improve the design, I introduced a dedicated database management layer and refactored core components to better align with software design principles.

**Key changes included:**

* Added `DatabaseHelper.java` to centralize database operations
* Moved SQL logic out of `LoginActivity.java` and `MainActivity.java`
* Updated login and registration to use centralized database methods
* Refactored inventory CRUD operations to use `DatabaseHelper`
* Implemented password hashing instead of storing plain-text passwords
* Added validation for empty fields and password constraints
* Added validation to prevent negative inventory values

This enhancement improved code organization, reduced redundancy, and strengthened application security.

---

### Enhancement Two: Algorithms and Data Structures

The second enhancement focused on improving how inventory data is processed, organized, and displayed to the user. The original implementation displayed raw database results without supporting efficient searching, sorting, or filtering.

To address this, I introduced object-based data handling and list-based processing to improve performance and usability.

**Key changes included:**

* Added `InventoryItem.java` to represent inventory records as objects
* Updated `DatabaseHelper.java` to return `ArrayList<InventoryItem>`
* Refactored `InventoryAdapter.java` to use object-based data
* Updated `MainActivity.java` to support list-based processing
* Implemented search functionality by item name
* Added sorting by item name and quantity (ascending and descending)
* Added individual low-stock filtering logic
* Added dropdown menu for sorting options
* Added toggle switch for low-stock view filtering
* Enabled item selection to autofill update fields

This enhancement improved how the application handles data in memory and made the inventory system significantly more user-friendly and efficient.

---

### Enhancement Three: Databases

The third enhancement focused on improving the database design and overall data integrity. The original database relied on item names as identifiers, which created limitations and risks when modifying or deleting records.

To improve reliability and scalability, the database structure was redesigned to use proper relational design principles.

**Key changes included:**

* Introduced a unique item ID as the primary key
* Updated update and delete operations to use item IDs instead of item names
* Expanded inventory table structure with additional fields:

  * Category
  * Low-stock limit
  * Date added
  * Last updated
* Added validation to prevent duplicate item names
* Added validation for empty inputs and negative values
* Updated filtering logic to use item-specific low-stock thresholds
* Separated item creation and update workflows into distinct forms

This enhancement significantly improved database reliability, scalability, and data consistency across the application.

---

## Files Included

### Original Code

* `LoginActivity.java`
* `MainActivity.java`
* `InventoryAdapter.java`

---

### Enhanced Code (Final Version)

* `LoginActivity.java`
* `MainActivity.java`
* `InventoryAdapter.java`
* `DatabaseHelper.java`
* `InventoryItem.java`
* `activity_data_display.xml`
* `grid_item.xml`

---

## Skills Demonstrated

Across all three enhancements, this project demonstrates a progression in software engineering skills, database design, and algorithmic thinking.

The first enhancement focused on improving software structure and applying separation of concerns. The second enhancement demonstrated the use of data structures and algorithms through list-based processing, searching, sorting, and filtering. The third enhancement focused on database design principles, improving data integrity, and implementing a more scalable relational structure.

Together, these improvements transformed the application from a basic functional prototype into a more structured, maintainable, and extensible mobile system.

---

## Course Outcomes Addressed

This capstone project supports the following course outcomes:

* **Outcome 2:** Communicating technical decisions through documentation, code comments, and written explanations across all enhancements
* **Outcome 3:** Applying software engineering principles, data structures, and database design improvements to enhance an existing application
* **Outcome 4:** Using Android Studio, Java, SQLite, and supporting tools to design and improve a working mobile application
* **Outcome 5:** Improving data integrity, security, and reliability through validation, secure coding practices, and database restructuring

---

## Final Reflection

This project demonstrates my ability to analyze an existing software system and progressively improve it through structured enhancements. Each milestone built upon the previous one, reinforcing key concepts in software design, data structures, and database architecture.

By the final enhancement, the application evolved into a more complete and reliable inventory management system with improved usability, stronger data handling, and better overall design consistency.
