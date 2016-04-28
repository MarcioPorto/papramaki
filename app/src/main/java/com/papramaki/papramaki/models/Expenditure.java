package com.papramaki.papramaki.models;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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


//    public String toString() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
//        if (category.equals("")) {
//            return "$" + amount + " on " + simpleDateFormat.format(date) + ".";
//        } else {
//            return "$" + amount + " on " + category.toString() + " on " + simpleDateFormat.format(date) + ".";
//        }
//    }

    public String dateToString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        return sdf.format(date);
    }

    public String formatAmount() {
        DecimalFormat formatter = new DecimalFormat("$0.00");
        return formatter.format(amount);
    }
}
