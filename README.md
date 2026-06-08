# Enhancement One: Software Design and Engineering

## Artifact Overview

This enhancement is based on my CS360 Mobile Inventory Application. The application was originally created in Android Studio using Java and SQLite. The app allows users to create an account, log in, and manage inventory items through a mobile interface. Once logged in, users can add inventory items, update quantities, delete items, and view the inventory in a GridView.

The original application was functional, but the code still had a lot of room for improvement. A large part of the database logic was written directly inside the activity files. This worked, but it made the activity files responsible for too many things at once.

## Enhancement Summary

For this enhancement, I focused on improving the overall code structure and making the application easier to maintain. I added `DatabaseHelper.java` to handle database setup and database operations. This allowed `LoginActivity.java` and `MainActivity.java` to focus more on user interaction and screen behavior instead of directly managing SQL logic.

The main changes include:

* Added `DatabaseHelper.java`
* Moved database logic out of `LoginActivity.java` and `MainActivity.java`
* Updated login and account creation to use `DatabaseHelper`
* Updated inventory add, update, delete, and load logic to use `DatabaseHelper`
* Added password hashing instead of storing passwords as plain text
* Added stronger validation for empty fields and password length
* Added validation to prevent negative inventory quantities

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

## Skills Demonstrated

This enhancement demonstrates software design, secure coding, database interaction, and code organization. The biggest improvement was separating database responsibilities from the activity files. This made the application cleaner, easier to explain, and easier to expand later.

This enhancement also improved security because the original application stored passwords as plain text. In the enhanced version, passwords are hashed before being stored or compared during login.

## Course Outcomes Addressed

This enhancement supports the following course outcomes:

* **Outcome 2:** Communicating technical decisions through code comments, documentation, and written explanation
* **Outcome 3:** Reviewing an existing application and improving the design
* **Outcome 4:** Using Android Studio, Java, SQLite, and secure coding practices to improve a working application
* **Outcome 5:** Improving security by addressing plain-text password storage

