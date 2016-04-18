package com.papramaki.papramaki.models;

import java.text.DecimalFormat;

public class  Budget {

    private double budget;
    private double balance;
    private double moneySpent;

    public Budget(double budget) {
        this.budget = budget;
        this.moneySpent = budget - balance;
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



}
