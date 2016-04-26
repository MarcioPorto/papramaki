package com.papramaki.papramaki.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulchery on 4/24/16.
 */
public class Category {

    private String name;
    private String color;
    private List<Expenditure> expenditures;
    private double sumCategory;



    public Category(String name, String color, List<Expenditure> expenditures){
        this.name = name;
        this.color = color;
        this.expenditures = expenditures;
    }

    public Category(){
        this.name = "";
        this.color = "";
        expenditures = new ArrayList<Expenditure>();
    }

    public double getSumCategory() {
        sumCategory = 0;
        for(Expenditure expenditure: expenditures){
            sumCategory += expenditure.getAmount();
        }
        return sumCategory;
    }

    public void setSumCategory(double sumCategory) {
        this.sumCategory = sumCategory;
    }
    public List<Expenditure> getExpenditures() {
        return expenditures;
    }

    public void setExpenditures(List<Expenditure> expenditures) {
        this.expenditures = expenditures;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
