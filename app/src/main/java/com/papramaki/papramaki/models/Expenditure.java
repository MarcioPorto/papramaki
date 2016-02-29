package com.papramaki.papramaki.models;

import java.util.Date;

public class Expenditure {

    private float amount;
    private String category;
    private Date date;

    public Expenditure(float amount, String category, Date date) {
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
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
}
