# 🏦 BankStone

**BankStone** is a command-line Java application that simulates a simple banking ledger system.  
It allows users to make deposits and payments, view and filter transactions, generate financial reports, and even email those reports as CSV files — all from the console.

---

## Features

- **Make Deposits and Payments**
  - Add new transactions (credits and debits) with descriptions and vendor names.
  - Automatically saved into a local CSV file (`transactions.csv`).

- **View Ledger**
  - See all transactions.
  - Filter by deposits, payments, or view custom reports.

- **Generate Reports**
  - Month-to-date, previous month, year-to-date, and previous year summaries.
  - Search transactions by vendor.
  - Build **custom reports** using multiple filters (date range, vendor, description, amount, etc.).

- **Email Reports**
  - Automatically generates a `report.csv` file.
  - Sends it as an email attachment via the **Resend API**.

---

## Technologies Used

- **Java 17+**
- **Resend API** (for email sending)
- **Java Stream API** (for filtering and reporting)
- **CSV I/O** using `FileReader`, `BufferedReader`, and `FileWriter`
- **Java Time API** (`LocalDate`, `LocalTime`, `LocalDateTime`)

---

## Project Structure

```
src/
└── com/
    └── pluralsight/
        ├── BankStone.java        # Main application class
        └── Transaction.java      # Model class for transactions (not shown here)
```

---

## Setup & Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/bankstone.git
cd bankstone
```

### 2. Add Dependencies
If using Maven, add the **Resend Java SDK** to your `pom.xml`:
```xml
<dependency>
  <groupId>com.resend</groupId>
  <artifactId>resend-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

Or, if using Gradle:
```groovy
implementation 'com.resend:resend-java:1.0.0'
```

### 3. Set Your Resend API Key
Before running, set your Resend API key as an environment variable:
```bash
export RESEND_API_KEY=your_resend_api_key_here
```
*(On Windows PowerShell:)*
```powershell
setx RESEND_API_KEY "your_resend_api_key_here"
```

### 4. Prepare CSV File
Ensure you have a `transactions.csv` file in the project root directory.  
If it doesn't exist, the app will create one as you add transactions.

Example header:
```
date|time|description|vendor|amount
```

---

## Navigation

**Main Menu:**
```
HOME
D) Make a Deposit
P) Make a Payment
L) View Ledger
X) Exit
```

**Ledger Menu:**
```
LEDGER
A) Display all entries
D) Display deposits
P) Display payments
R) Go to Reports
H) Home
```

**Reports Menu:**
```
REPORTS
1) Month To Date
2) Previous Month
3) Year To Date
4) Previous Year
5) Search by Vendor
6) Custom Search
0) Back
```

---

## Example Flow

1. Start the program → choose **Deposit** (`D`)  
2. Enter description, vendor, and amount.  
3. Choose **Ledger → All Entries** to view transactions.  
4. Generate a **custom report** and email it directly through the CLI.  

---

## Emailing Reports

When you generate a report (via **Custom Search**), the app automatically:
1. Saves filtered results to `report.csv`
2. Prompts you for an email address
3. Sends the file using your Resend account

---

## ⚠️ Notes

- The `transactions.csv` file must be in the same directory where the program runs.
- All amounts are stored as **positive (deposits)** or **negative (charges)**.
- Make sure your Resend API key is valid; otherwise, email sending will fail.

