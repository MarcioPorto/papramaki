package com.papramaki.papramaki.models;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Defines the expenditure model.
 * An expenditure has an amount, creation date, and a category id which links
 * it to the category of the expenditure
 */
public class Expenditure {

    private double amount;
    private int category_id;
    private Date date;

    public Expenditure(double amount, int category_id, Date date) {
        this.amount = amount;
        this.category_id = category_id;
        this.date = date;
    }

    public Expenditure(){
        this.amount = 0;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCategoryId() { return category_id; }

    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
