package com.pluralsight;

import java.util.Scanner;

public class BankStone {
    public static Scanner myScanner = new Scanner(System.in);
    public static void main(String[] args) {
        home(); // start application and display home screen
    }

    public static String askUser(String question) {
        try {
            return myScanner.nextLine();
        }
        catch (Exception e) { // usually only catches if there is something wrong with the scanner
            System.out.println("Error prompting for user input");
            return "";
        }
    }

    public static void home() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("""
                    HOME
                    
                    Welcome!
                    """);

            String response = askUser("""
                    Select an option below:
                    D) Make a Deposit
                    P) Make a Payment (Debit)
                    L) View Ledger
                    X) Exit
                    """);

            if (response == null || response.isEmpty()) {
                System.out.println("Response not valid. Please try again");
                continue;
            }
            else {
                switch (response.toLowerCase()) {
                    case "d": {
                        makeDeposit();
                    }

                    case "p": {
                        makePayment();
                    }

                    case "l": {
                        viewLedger();
                    }

                    case "x": {
                        isRunning = false;
                        break;
                    }

                }
            }

        }


    }

    public static void makeDeposit() {

    }

    public static void viewLedger() {

    }

    public static void makePayment() {

    }
}
