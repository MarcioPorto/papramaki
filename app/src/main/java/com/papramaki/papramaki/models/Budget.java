package com.papramaki.papramaki.models;

import java.text.DecimalFormat;

public class  Budget {

    private double budget;
    private double balance;

    public Budget(double budget) {
        this.budget = budget;
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

    @Override
    public String toString() {
        return Double.toString(budget) + "0";
    }



}
