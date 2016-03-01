package com.papramaki.papramaki.models;

public class Budget {

    private float budget;

    public Budget(float budget) {
        this.budget = budget;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return Float.toString(budget);
    }
}
