package com.papramaki.papramaki.models;

import java.util.ArrayList;
import java.util.List;

public class History {

    private List<Expenditure> expenditures;

    public History() {
        this.expenditures = new ArrayList<>();
    }

    public List<Expenditure> getExpenditures() {
        return expenditures;
    }

    public void setExpenditures(List<Expenditure> expenditures) {
        this.expenditures = expenditures;
    }


}
