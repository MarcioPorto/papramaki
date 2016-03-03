package com.papramaki.papramaki.models;

import java.util.Date;

public class Expenditure {

    private double amount;
    private String category;
    private Date date;

    public Expenditure(double amount, String category, Date date) {
        this.amount = amount;
        this.category = category;
        this.date = date;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String toString() { return "$" + amount + ", " + date.toString() + ", " + category.toString(); }
}
