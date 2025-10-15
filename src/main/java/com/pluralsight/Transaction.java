package com.pluralsight;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Transaction {
    private LocalDateTime dateTime;
    private String description;
    private String vendor;
    private double amount;

    public Transaction(LocalDateTime dateTime, String description, String vendor, double amount) {
        this.dateTime = dateTime;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public Transaction(String description, String vendor, double amount) {
        this.dateTime = LocalDateTime.now();
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public void display() {
        String date = getDateTime().toLocalDate().toString();
        String time = getDateTime().toLocalTime().toString();
        String description = getDescription();
        String vendor = getVendor();
        double amount = getAmount();
        System.out.printf("\nDate: %s\nTime: %s\nDescription: %s\nVendor: %s\nAmount: %.2f", date, time, description, vendor, amount);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
