MAIN/
│
├── BillingService.java     # Handles billing logic (calculation, totals)
├── HistoryManager.java    # Stores and retrieves bill history
├── Main.java              # Entry point of the application
├── Order.java             # Represents a customer's order
├── RestaurantUI.java      # UI (frontend) for interaction


How to Run
Compile all files
    javac *.java
Run the application
    java Main

--------------

How the System Works (Logic + Backend Flow)

Your project follows a layered architecture:

UI (RestaurantUI)
   ↓
Service Layer (BillingService)
   ↓
Model (Order)
   ↓
Storage (HistoryManager)
Complete Flow (Step-by-Step)
1) User Interaction (Frontend)

RestaurantUI.java handles:

Input (item name, quantity, price)
Button clicks (Add, Generate Bill, History)

When the user clicks “Add Item”:

Order order = new Order(item, quantity, price);
2) Order Creation (Data Layer)

Order.java acts as a model class.

It stores:

Item name
Quantity
Price

Example:

Order o = new Order("Burger", 2, 50);
3) Billing Logic (Core Backend)

BillingService.java processes all orders.

Main responsibilities:

Calculate total bill
Manage list of orders
Format bill output

Logic:

total += order.getQuantity() * order.getPrice();

It converts raw data into a final bill string.

4) Bill Generation

After calculation, a final bill string is created.

Example:

Burger x2 = 100
Pizza x1 = 150
----------------
Total = 250
5) History Storage (Backend Memory)

HistoryManager.java stores bills.

When a bill is generated:

HistoryManager.addBill(bill);

Internally:

List<String> history = new ArrayList<>();

Each bill is stored in memory.

6) Viewing History

When the user clicks the History button:

UI calls:

HistoryManager.showHistory();

It iterates through stored bills:

for (String bill : history)

Displays all previous bills.