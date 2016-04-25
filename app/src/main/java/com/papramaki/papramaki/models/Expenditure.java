package com.papramaki.papramaki.models;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Expenditure {

    private double amount;
    private String category;
    private Date date;

    public Expenditure(double amount, String category, Date date) {
        this.amount = amount;
        if(!(category.equals(""))) {
            this.category = category;
        }
        else {
            this.category = "Uncategorized";      // assigns all expenditures not given a category by user to "uncategorized"
        }
        this.date = date;
    }

    public Expenditure(){}


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() { return category; }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        if (category.equals("")) {
            return "$" + amount + " on " + simpleDateFormat.format(date) + ".";
        } else {
            return "$" + amount + " on " + category.toString() + " on " + simpleDateFormat.format(date) + ".";
        }
    }

    public String dateToString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        return sdf.format(date);
    }

    public String formatAmount() {
        DecimalFormat formatter = new DecimalFormat("$0.00");
        return formatter.format(amount);
    }
}
