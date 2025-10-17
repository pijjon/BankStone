package com.pluralsight;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

public class BankStone {
    public static Scanner myScanner = new Scanner(System.in); // static scanner declaration

    public static ArrayList<Transaction> ledger = new ArrayList<>(); // static ArrayList declaration

    public static void main(String[] args) {
        preload(); // preload the ArrayList ledger with data already existing in csv file
        home(); // start application and display home screen
    }

    // method for displaying the home screen
    public static void home() {
        // while loop to keep the screen up even if there are misinputs
        boolean isRunning = true;
        while (isRunning) {

            String response = askUser("""
                    
                    HOME
                    
                    Welcome!
                    
                    Select an option below:
                    D) Make a Deposit
                    P) Make a Payment (Debit)
                    L) View Ledger
                    X) Exit
                    """);

            // input validation for empty input
            if (response.isEmpty()) {
                System.out.println("Response not valid. Please try again");
            } else {
                // control flow for user input
                switch (response.toLowerCase()) {
                    case "d": {
                        // invoke the makeTransaction() method and pass in deposit for tx type
                        makeTransaction("deposit");
                        break;
                    }

                    case "p": {
                        // invoke the makeTransaction() method and pass in charge for tx type
                        makeTransaction("charge");
                        break;
                    }

                    case "l": {
                        // display ledger screen
                        ledger();
                        break;
                    }

                    case "x": {
                        // exit the program by stopping the while loop
                        isRunning = false;
                        break;
                    }

                }
            }

        }
    }

    // static method for making transactions
    public static void makeTransaction(String type) {
        String description = askUser("Provide a description for this " + type);
        String vendor = askUser("Provide the name of the vendor");
        double amount;
        if (type.equals("deposit")) {
            amount = Math.abs(askUserDouble("How much money would you like to deposit?"));

        } else {
            amount = -Math.abs(askUserDouble("How much was the charge?"));
        }

        Transaction transaction = new Transaction(description, vendor, amount);

        // with each transaction made we:
        ledger.add(transaction); // store in ArrayList ledger
        storeInCSV(transaction, "transactions.csv");  // and write it to the csv file

    }

    // method for writing lines to CSV file
    public static void storeInCSV(Transaction transaction, String filePath) {

        System.out.println("storing in CSV");

        // get and format date
        LocalDateTime dateTime = transaction.getDateTime();
        LocalDate date = dateTime.toLocalDate();

        // get and format time
        LocalTime rawTime = dateTime.toLocalTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = rawTime.format(formatter);

        // get other info to construct new CSV line
        String description = transaction.getDescription();
        String vendor = transaction.getVendor();
        double amount = transaction.getAmount();

        // build CSV line
        String line = date + "|" + time + "|" + description + "|" + vendor + "|" + amount;

        try (
                FileWriter fileWriter = new FileWriter(filePath, true); // pass in true to enable append mode (to not overwrite the whole file)
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        ) {
            // write line to file
            bufferedWriter.newLine();
            bufferedWriter.write(line);

        } catch (IOException e) { // cath input errors
            System.out.println("Error writing file");
        }

    }

    // method for displaying ledger screen
    public static void ledger() {
        boolean isRunning = true;
        while (isRunning) { // keep display up even after misinput
            String response = askUser("""
                    
                    LEDGER
                    
                    A) Display all entries
                    D) Display deposits
                    P) Display payments
                    R) Go to Reports
                    H) Home
                    
                    Please select an option:
                    """);

            // control flow for user input
            switch (response.toLowerCase()) {
                case "a":
                    // display all tx
                    viewAll();
                    break;

                case "d":
                    // display positive tx
                    viewDeposits();
                    break;

                case "p":
                    //display negative tx
                    viewCharges();
                    break;

                case "r":
                    // display the reports screen
                    reports();
                    break;

                case "h":
                    // back to home display loop
                    isRunning = false;
                    break;
            }
        }
    }

    // method for viewing all transactions
    public static void viewAll() {
        // loop through ArrayList
        for (Transaction transaction : ledger) {
            // use display() class method
            transaction.display();
        }
    }

    // method for viewing all deposits
    public static void viewDeposits() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction -> transaction.getAmount() > 0) // filter by positive amounts
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    // method for viewing all charges/payments
    public static void viewCharges() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction -> transaction.getAmount() < 0) // filter by negative amounts
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    // method for displaying the reports screen
    public static void reports() {
        boolean isRunning = true;
        while (isRunning) { // keep screen running
            String response = askUser("""
                    
                    REPORTS
                    
                    1) Month To Date
                    2) Previous Month
                    3) Year To Date
                    4) Previous Year
                    5) Search by Vendor
                    6) Custom Search
                    0) Back
                    """);

            // control flow for user input
            switch (response) {
                case "1":
                    // display month to date tx
                    monthToDate();
                    break;
                case "2":
                    // display previous month tx
                    previousMonth();
                    break;
                case "3":
                    // display year to date tx
                    yearToDate();
                    break;
                case "4":
                    // display prev year tx
                    previousYear();
                    break;
                case "5":
                    // display tx by vendor
                    searchByVendor();
                    break;
                case "6":
                    // display customSearch() screen
                    customSearch();
                    break;
                case "0":
                    // previous screen by breaking loop
                    isRunning = false;
            }
        }
    }

    // preload ArrayList
    public static void preload() {
        // initialize FileReader and Buffered Reader in try-with-resources for auto closing
        try (FileReader fileReader = new FileReader("transactions.csv");
             BufferedReader bufferedReader = new BufferedReader(fileReader);) {

            // iterate through each line of csv file to parse data
            String line = bufferedReader.readLine(); // this is just the header line, not needed

            // read next line and make sure it is not null
            while ((line = bufferedReader.readLine()) != null) {
                Transaction transaction = getTransaction(line);

                // add each new object to ledger ArrayList
                ledger.add(transaction);
            }

        } catch (IOException e) {
            System.out.println("Error reading csv file");
        }
    }

    // convert lines of text from csv into Transaction objects and return the object
    private static Transaction getTransaction(String line) {
        String[] parts = line.split("\\|");
        LocalDate date = LocalDate.parse(parts[0]);
        LocalTime time = LocalTime.parse(parts[1]);
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        String description = parts[2];

        String vendor = parts[3];

        double amount = Double.parseDouble(parts[4]);

        // create new objects with overloaded constructor function, passing in data
        return new Transaction(dateTime, description, vendor, amount);
    }

    // method for prompting user for String input
    public static String askUser(String question) {
        try {
            System.out.println(question);
            return myScanner.nextLine().trim();
        } catch (Exception e) { // usually only catches if there is something wrong with the scanner
            System.out.println("Error prompting for user input");
            return "";
        }
    }

    //method for prompting user for Double input
    public static double askUserDouble(String question) {
        while (true) { // keep looping indefinitely until we get a correct input
            try {
                System.out.println(question);
                double response = myScanner.nextDouble();
                myScanner.nextLine(); // eat the line
                return response; // until a return statement is reached (return breaks the while loop)
            } catch (InputMismatchException e) {
                System.out.println("Incorrect input type. Try again!");
                myScanner.nextLine(); // clear the buffer if wrong input type
            } catch (NoSuchElementException | IllegalStateException e) {
                e.printStackTrace();
                System.out.println("Something went wrong. Try again!");
            }
        }
    }

    // method for displaying tx this month
    public static void monthToDate() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction ->
                        transaction.getDateTime().getMonth() == LocalDateTime.now().getMonth() &&
                                transaction.getDateTime().getYear() == LocalDateTime.now().getYear())
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    // method for displaying tx last month
    public static void previousMonth() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction ->
                        transaction.getDateTime().getMonth() == LocalDateTime.now().minusMonths(1).getMonth() &&
                                transaction.getDateTime().getYear() == LocalDateTime.now().getYear())
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    // method for displaying tx this year
    public static void yearToDate() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction -> transaction.getDateTime().getYear() == LocalDateTime.now().getYear())
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    // method for displaying tx last year
    public static void previousYear() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction -> transaction.getDateTime().getYear() == LocalDateTime.now().minusYears(1).getYear())
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    // method for displaying tx by vendor
    public static void searchByVendor() {
        String vendor = askUser("What vendor would you like to search for in transactions?");
        List<Transaction> filtered = ledger.stream()
                .filter(transaction -> transaction.getVendor().equalsIgnoreCase(vendor))
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    //method for displaying custom search screen
    public static void customSearch() {
        // initialize filter values
        String startDateStr = null;
        String endDateStr = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        String vendor = null;
        String description = null;
        String type = null;
        Double startAmount = null;
        Double endAmount = null;

        while (true) { // keep menu running
            String question = String.format("""
                    
                    CUSTOM REPORT
                    
                    Please select a filter below to change:
                    
                    1) Start Date:             %s
                    2) End Date:               %s
                    3) Vendor:                 %s
                    4) Description:            %s
                    5) Type (Deposit/Payment): %s
                    6) Start Amount:           %.2f
                    7) End Amount:             %.2f
                    
                    G) Generate Report
                    X) Exit to Reports
                    
                    """, startDateStr, endDateStr, vendor, description, type, startAmount, endAmount); // use formatting to display current filter details

            String response = askUser(question); // prompt for user selection

            // control flow for user inputs for changing filter items
            switch (response.toLowerCase()) {

                // option for start date filter
                case "1":
                    String update1 = askUser("What start date would you like to search by? (Leave blank to skip)");
                    if (!isNullOrEmpty(update1)) {
                        startDate = LocalDate.parse(update1);
                        startDateStr = startDate.toString(); // necessary for String formatting above
                        System.out.println(startDate);
                    }
                    break;

                // option for end date filter
                case "2":
                    String update2 = askUser("What end date would you like to search by? (Leave blank to skip)");
                    if (!isNullOrEmpty(update2)) {
                        endDate = LocalDate.parse(update2);
                        endDateStr = endDate.toString();
                        System.out.println(endDate);
                    }
                    break;

                // option for vendor filter
                case "3":
                    String update3 = askUser("What vendor would you like to search by? (Leave blank to skip)");
                    if (!isNullOrEmpty(update3)) {
                        vendor = update3;
                    }
                    break;

                // option for description filter
                case "4":
                    String update4 = askUser("What description would you like to search by? (Leave blank to skip)");
                    if (!isNullOrEmpty(update4)) {
                        description = update4;
                    }
                    break;

                // option for tx type filter
                case "5":
                    while (true) {
                        String update5 = askUser("What transaction type would you like to search by? (Leave blank to skip)\nD) Deposits or P)Payments/Charges ");
                        if (!isNullOrEmpty(update5)) {
                            if (update5.equalsIgnoreCase("d")) {
                                type = "deposits";
                                break;
                            }
                            else if (update5.equalsIgnoreCase("p")) {
                                type = "payments";
                                break;
                            }
                            else {
                                System.out.println("Improper input!");
                                continue;
                            }
                        }
                        break;

                    }
                    break;

                // option for start amount filter
                case "6":
                    String update6 = askUser("What minimum amount would you like to filter for? (Leave blank to skip)");
                    if (!isNullOrEmpty(update6)) {
                        startAmount = Double.parseDouble(update6);
                    }
                    break;

                // option for end amount filter
                case "7":
                    String update7 = askUser("What maximum amount would you like to filter for? (Leave blank to skip)");
                    if (!isNullOrEmpty(update7)) {
                        endAmount = Double.parseDouble(update7);
                    }
                    break;

                // option for filtering ledger based on custom filters
                case "g":
                    customFilter(
                            startDateStr,
                            endDateStr,
                            startDate,
                            endDate,
                            vendor,
                            description,
                            type,
                            startAmount,
                            endAmount);


                case "x":
                return;
            }
        }


    }

    // method for filtering in customSearch()
    private static void customFilter(
            String startDateStr,
            String endDateStr,
            LocalDate startDate,
            LocalDate endDate,
            String vendor,
            String description,
            String type,
            Double startAmount,
            Double endAmount) {

        List<Transaction> filtered = ledger.stream()
                // filter by elements after startDate if it exists
                .filter(transaction -> (isNullOrEmpty(startDateStr) || transaction.getDateTime().isAfter(startDate.atStartOfDay())))

                // filter by elements before endDate if it exists
                .filter(transaction -> (isNullOrEmpty(endDateStr) || transaction.getDateTime().isBefore(endDate.atStartOfDay())))

                // filter by elements that contain vendor name
                .filter(transaction -> (isNullOrEmpty(vendor) || transaction.getVendor().toLowerCase().contains(vendor.toLowerCase())))

                // filter by elements that contain description
                .filter(transaction -> (isNullOrEmpty(description) || transaction.getDescription().toLowerCase().contains(description.toLowerCase())))

                // filter elements by transaction type if it exists (positive amounts if deposits, negative amounts if payments
                .filter(transaction -> (isNullOrEmpty(type) || (type.equals("deposits") ? transaction.getAmount() > 0 : transaction.getAmount() < 0)))

                // filter by elements in startAmount and endAmount range
                .filter(transaction -> (startAmount == null || Math.abs(transaction.getAmount()) > Math.abs(startAmount)) && (endAmount == null || Math.abs(transaction.getAmount()) < Math.abs(endAmount)))

                // save as List
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display(); // display each
            storeInCSV(transaction, "report.csv"); // store in new report.csv file

        }
        try {
            emailFile("report.csv"); // invoke method for emailing the generated report
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // method for checking isf value is null or empty
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    // method for using Resend package to send email with attachment
    public static void emailFile(String path) throws IOException {
        String email = askUser("What is the email you'd like to send this file to?");

        // resend api key is stored in the environment variable
        Resend resend = new Resend(System.getenv("RESEND_API_KEY"));

        // Read your file (e.g., invoice.pdf) as bytes
        byte[] fileBytes = Files.readAllBytes(Path.of("report.csv"));

        // Encode the bytes to Base64 string
        String base64Content = Base64.getEncoder().encodeToString(fileBytes);

        // Create the attachment object with filename and Base64 content
        Attachment attachment = Attachment.builder()
                .fileName("report.csv") // used to specify the name of the file
                .content(base64Content)
                .build();

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("BankStone App <onboarding@resend.dev>")
                .to(email)
                .subject("Report Generated")
                .html("<strong>Your Report is Attached</strong>")
                .attachments(attachment) // actually attach the file I made
                .build();

        try {
            // attempt to send the email that was built
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
    }

}
