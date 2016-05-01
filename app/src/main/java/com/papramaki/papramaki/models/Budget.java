package com.papramaki.papramaki.models;

import java.text.DecimalFormat;
import java.util.Date;

public class  Budget {

    private double budget;
    private double balance;
    private double moneySpent;
    private int duration;
    private int id;
    private boolean expired;
    private Date expirationDate;

    public Budget(double budget, int id) {
        this.budget = budget;
        this.moneySpent = budget - balance;
        this.id = id;
    }
    public Budget(){
        this.budget = 0;
        this.duration = 0;
        this.id = 0;
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

//    /**
//     * Option to create a budget with a duration (in weeks)
//     * @param budget
//     * @param duration
//     */
//    public Budget(double budget, int duration) {
//        this.budget = budget;
//        this.duration = duration;
//        this.moneySpent = budget - balance;
//    }

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

    public double getBalance(){
        return balance;
    }

    public String getFormattedBalance () {
        DecimalFormat formatter = new DecimalFormat("$0.00");
        return formatter.format(balance);
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

    public double getMoneySpent() {
        this.moneySpent = this.budget - this.balance;
        return moneySpent;
    }

    public String getFormattedMoneySpent() {
        DecimalFormat formatter = new DecimalFormat("$0.00");
        return formatter.format(getMoneySpent());
    }

    @Override
    public String toString() {
        return Double.toString(budget) + "0";
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
}
