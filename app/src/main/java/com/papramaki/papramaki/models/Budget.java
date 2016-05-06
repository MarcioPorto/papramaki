package com.papramaki.papramaki.models;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class  Budget {

    private double budget;
    private int duration;
    private int id;
    private boolean expired;
    private Date creationDate;
    private Date expirationDate;

    public Budget(double budget, int id) {
        this.budget = budget;
        this.id = id;
        this.creationDate = Calendar.getInstance().getTime();
        this.expirationDate = Calendar.getInstance().getTime();
    }

    public Budget(){
        this.budget = 0;
        this.duration = 0;
        this.id = 0;
        this.creationDate = Calendar.getInstance().getTime();
        this.expirationDate = Calendar.getInstance().getTime();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getBudget() {
        return budget;
    }

    public String getFormattedBudget() {
        DecimalFormat formatter = new DecimalFormat("$0.00");
        return formatter.format(budget);
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }


    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return Double.toString(budget) + "0";
    }

}
