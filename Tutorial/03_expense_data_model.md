# Chapter 3: Expense Data Model

In the previous chapters, we saw how the [Controller](01_web_request_handler__controller_.md) handles requests and how the [View](02_user_interface_views__thymeleaf_templates_.md) displays information using data like `${expenses}` or binds forms to an `${expense}` object. But what exactly *is* an `expense` in our code? How does the application know what pieces of information make up a single expense record?

That's where the **Expense Data Model** comes in!

## What's the Big Idea? The Blueprint for Expenses

Imagine you want to keep track of your expenses using index cards. To make sure you record the same information every time, you might create a standard template or **blueprint** for your cards:

*   **What did I buy?** _______________ (Description)
*   **How much did it cost?** $_________ (Amount)
*   **When did I buy it?** __________ (Date)
*   **What type of spending?** _________ (Category)
*   **Any extra details?** __________ (Notes)

This blueprint ensures every expense card has the necessary details recorded in a consistent way.

In our Java application, the **Expense Data Model** acts exactly like this blueprint. It's a Java class (specifically, `Expense.java`) that defines:

1.  **The Properties:** What pieces of information (fields) make up a single expense record (e.g., description, amount, date, category).
2.  **The Data Types:** What kind of information each property holds (e.g., description is text, amount is a number, date is a date).
3.  **The Rules (Validation):** Any constraints the data must follow (e.g., the amount must be a positive number, the description cannot be empty).

This blueprint is crucial for organizing our data, ensuring consistency, and making it easy for different parts of the application (like the Controller, View, and database logic) to understand and work with expense information.

## Meet the `Expense` Class Blueprint

Let's look at the blueprint itself, defined in the `Expense.java` file.

```java
// File: src/main/java/com/expensetracker/model/Expense.java

package com.expensetracker.model; // Defines the folder where this blueprint lives

// Imports: Bring in tools we need from Java libraries
import jakarta.persistence.*; // Tools for database interaction
import jakarta.validation.constraints.*; // Tools for data validation rules
import lombok.Data; // A helper tool to write less boilerplate code
import java.math.BigDecimal; // For precise decimal numbers (like money)
import java.time.LocalDate; // For dates without time

// @Entity: Tells the system this blueprint maps to a database table
@Entity
// @Data: Lombok shortcut! Automatically adds standard methods (getters/setters)
// so we can easily get/set values for the fields below.
@Data
public class Expense { // Our blueprint definition starts here

    // @Id: Marks this field as the unique identifier (like a record number)
    @Id
    // @GeneratedValue: Tells the database to automatically generate this ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // The unique ID for the expense (a whole number)

    // @NotBlank: Rule! This text field cannot be empty or just whitespace.
    @NotBlank(message = "Description is required")
    private String description; // What was purchased (text)

    // @NotNull: Rule! This field cannot be missing (null).
    @NotNull(message = "Amount is required")
    // @Positive: Rule! This number must be greater than zero.
    @Positive(message = "Amount must be positive")
    private BigDecimal amount; // How much it cost (a precise decimal number)

    // @NotNull: Rule! The date cannot be missing.
    @NotNull(message = "Date is required")
    private LocalDate date; // When it was purchased (date only)

    // @NotBlank: Rule! Category text cannot be empty.
    @NotBlank(message = "Category is required")
    private String category; // Type of expense (e.g., Food, Transport) (text)

    // Optional field, no validation rules here
    private String notes; // Any extra details (text)
}
```

**Breaking it Down:**

1.  **`package com.expensetracker.model;`**: Just like organizing files into folders, packages organize our Java classes. This tells us the `Expense` class is in the `model` "folder".
2.  **`import ...;`**: These lines bring in pre-written Java code (classes) that provide useful features, like handling dates (`LocalDate`), precise numbers (`BigDecimal`), database mapping (`@Entity`, `@Id`, etc.), and validation rules (`@NotBlank`, `@NotNull`, `@Positive`).
3.  **`@Entity`**: This is a special marker (annotation) from JPA (Java Persistence API). It signals that objects created from this `Expense` class blueprint are intended to be stored in a database. Think of it as tagging the blueprint as "database-ready". We'll learn more about how this connects to the database in [Chapter 5: Data Persistence (Repository)](05_data_persistence__repository_.md).
4.  **`@Data`**: This annotation comes from a library called Lombok. It's a handy shortcut that automatically generates common methods for us behind the scenes, like `getDescription()`, `setDescription(String newDescription)`, `getAmount()`, `setAmount(BigDecimal newAmount)`, etc. These methods (called getters and setters) are the standard way to access and modify the values of the fields in an object.
5.  **`public class Expense { ... }`**: This line declares our blueprint and names it `Expense`. Everything inside the curly braces `{ ... }` defines the structure of an expense.
6.  **Fields (`private Long id;`, `private String description;`, etc.)**: These lines declare the actual data fields (properties) that each `Expense` object will hold.
    *   `private`: This keyword means the data inside the object is generally kept private and should be accessed using the getter/setter methods (which `@Data` provides).
    *   `Long`, `String`, `BigDecimal`, `LocalDate`: These are the Java data types specifying what kind of information each field holds (Long whole number, text, precise decimal, date).
7.  **`@Id` and `@GeneratedValue`**: These annotations work together for the `id` field. They tell the database system that `id` is the *primary key* (unique identifier) for each expense record and that the database should automatically assign a unique ID whenever a new expense is saved.
8.  **Validation Annotations (`@NotBlank`, `@NotNull`, `@Positive`)**: These annotations define the rules for the data.
    *   `@NotBlank`: Used for `String` fields (like `description`, `category`). It means the field cannot be empty *and* cannot consist only of whitespace characters.
    *   `@NotNull`: Used for object types (like `amount`, `date`). It means the field cannot be missing entirely (cannot be `null`).
    *   `@Positive`: Used for number fields (like `amount`). It means the value must be strictly greater than zero.
    *   `message = "..."`: This part provides the error message that will be shown to the user if the rule is violated. Remember how the [Controller](01_web_request_handler__controller_.md) checked for errors (`BindingResult`)? These annotations power that validation process!

## How is the Blueprint Used?

Now that we have this `Expense` blueprint, let's see how other parts of the application use it:

1.  **Creating New Expense Objects (Controller/Service):** When you want to add a new expense, the application creates a new, empty "form" based on the blueprint.
    ```java
    // Somewhere in the Controller or Service...
    Expense newExpense = new Expense(); // Creates a blank Expense object
    // Now we can fill in the details:
    newExpense.setDescription("Coffee");
    newExpense.setAmount(new BigDecimal("3.50"));
    newExpense.setDate(LocalDate.now()); // Set date to today
    newExpense.setCategory("Food");
    // Now this 'newExpense' object holds the data for one specific expense.
    ```
    This `newExpense` object is an *instance* of the `Expense` class – a specific expense record created using the `Expense` blueprint.

2.  **Receiving Data from Forms (Controller):** When you submit the "Add Expense" form, the framework takes the data you entered and uses the `Expense` blueprint to create an object holding that data.
    ```java
    // In ExpenseController (from Chapter 1)
    @PostMapping // Handles form submission
    public String saveExpense(@ModelAttribute("expense") Expense expense, ...) {
        // Spring automatically creates an 'expense' object
        // using the blueprint and fills it with form data.
        // If description entered was "Lunch", then expense.getDescription() would return "Lunch".
        // ... then we save this 'expense' object ...
    }
    ```

3.  **Displaying Data (View - Thymeleaf):** When showing the list of expenses, the [View](02_user_interface_views__thymeleaf_templates_.md) accesses the fields defined in the blueprint for each expense object.
    ```html
    <!-- In expenses/list.html (from Chapter 2) -->
    <tr th:each="exp : ${expenses}"> <!-- Loop through Expense objects -->
      <!-- Access the 'date' field defined in the Expense blueprint -->
      <td th:text="${#temporals.format(exp.date, 'MMM dd, yyyy')}">Jan 01</td>
      <!-- Access the 'description' field -->
      <td th:text="${exp.description}">Sample Expense</td>
      <!-- Access the 'amount' field -->
      <td th:text="${#numbers.formatCurrency(exp.amount)}">$0.00</td>
      <!-- ... and so on -->
    </tr>
    ```
    Here, `exp` represents one specific `Expense` object (one filled-out blueprint) in each step of the loop, and `exp.date`, `exp.description`, `exp.amount` access the data stored in its fields.

4.  **Storing and Retrieving (Service/Repository):** The parts of the application responsible for saving data to and fetching data from the database ([Service](04_business_logic__service_.md) and [Repository](05_data_persistence__repository_.md)) work directly with these `Expense` objects. The `@Entity` tag on our blueprint tells the database layer how to map the object's fields to columns in a database table.

## Connecting to the Database: A Quick Peek

Because we marked our `Expense` class with `@Entity`, the underlying persistence framework (JPA/Hibernate) knows how to translate between `Expense` objects in our Java code and rows in a database table (likely named `expense`).

Conceptually, the mapping looks like this:

| `Expense` Class (Java Object)        | `expense` Table (Database Row) |
| :----------------------------------- | :----------------------------- |
| `private Long id;`                   | `id` column (Primary Key, BIGINT) |
| `private String description;`        | `description` column (VARCHAR) |
| `private BigDecimal amount;`         | `amount` column (DECIMAL)      |
| `private LocalDate date;`            | `date` column (DATE)           |
| `private String category;`           | `category` column (VARCHAR)    |
| `private String notes;`              | `notes` column (VARCHAR)       |

When the [Repository](05_data_persistence__repository_.md) saves an `Expense` object, it generates an SQL `INSERT` statement to add a new row to the `expense` table with the corresponding values. When it fetches expenses, it generates an SQL `SELECT` statement and converts the resulting rows back into `Expense` objects. The `@Entity` blueprint makes this translation possible.

## Conclusion

You've now learned about the **Expense Data Model**, represented by the `Expense` class in our project. This class acts as the fundamental **blueprint** for any expense record.

*   It defines the **structure** (fields like `description`, `amount`, `date`).
*   It specifies the **data types** for each field.
*   It includes **validation rules** (`@NotBlank`, `@Positive`, etc.) to ensure data quality.
*   It's marked with `@Entity` to link it to a database table for storage ([Chapter 5](05_data_persistence__repository_.md)).
*   It's used throughout the application: by the [Controller](01_web_request_handler__controller_.md) to handle form data, by the [View](02_user_interface_views__thymeleaf_templates_.md) to display details, and by the backend layers to save and retrieve expense information.

Understanding this data model is key because it's the core data structure we're working with. Now that we know *what* an expense looks like in our code, how do we actually *manage* these expenses – saving them, finding them, deleting them, applying rules? That's the job of the next component we'll explore.

Next up: [Chapter 4: Business Logic (Service)](04_business_logic__service_.md)

---

Generated by [AI Codebase Knowledge Builder](https://github.com/The-Pocket/Tutorial-Codebase-Knowledge)