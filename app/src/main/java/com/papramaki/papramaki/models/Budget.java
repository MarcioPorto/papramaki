package com.papramaki.papramaki.models;

public class Budget {

    private double budget;

    public Budget(float budget) {
        this.budget = budget;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return Double.toString(budget) + "0";
    }

}
