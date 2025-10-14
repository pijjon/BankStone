package com.pluralsight;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class BankStone {
    public static Scanner myScanner = new Scanner(System.in);
    public static void main(String[] args) {

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

    }

    public static void deposit() {

    }

    public static void ledger() {

    }

    public static void makePayment() {

    }
}
