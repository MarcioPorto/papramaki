package com.papramaki.papramaki.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Expenditure {

    private double amount;
    private String category;
    private Date date;
    private String itemTitle;

    public Expenditure(double amount, String category, Date date) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.itemTitle = "$" + amount;   // default itemTitle is expenditure amount
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

    public String getItemTitle() { return itemTitle; }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    /**
     * Lets user set itemTitle to a String other than default assigned in constructor
     * @param title
     */
    public Expenditure(String title) {
        this.itemTitle = title;
    }

    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (category.equals("")) {
            return "$" + amount + " on " + simpleDateFormat.format(date) + ".";
        } else {
            return "$" + amount + " on " + category.toString() + " on " + simpleDateFormat.format(date) + ".";
        }
    }

}
