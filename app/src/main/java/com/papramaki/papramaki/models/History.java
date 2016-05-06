package com.papramaki.papramaki.models;

import java.util.ArrayList;
import java.util.List;


/**
 * A History corresponds to a list of the expenditures made during the current budget period.
 * It also contains the total sum of these expenditures.
 */
public class History {

    private List<Expenditure> expenditures;
    private double expenditureSum;

    public History() {
        this.expenditures = new ArrayList<>();
        this.expenditureSum = 0;
    }

    public List<Expenditure> getExpenditures() {
        return expenditures;
    }

    public void setExpenditures(List<Expenditure> expenditures) {
        this.expenditures = expenditures;
    }

    public double getExpenditureSum() {
        return expenditureSum;
    }

    public void setExpenditureSum(double expenditureSum) {
        this.expenditureSum = expenditureSum;
    }
}
