package com.papramaki.papramaki.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulchery on 4/24/16.
 */

/**
 * Defines the category model.
 * A category has a name, color, a list of expenditures, a sum that corresponds to the total
 * sum of the expenditures, and an id number.
 */
public class Category {

    private String name;
    private String color;
    private List<Expenditure> expenditures;
    private double sumCategory;
    private int id;


    public Category(String name, String color, int id){
        this.name = name;
        this.color = color;
        this.expenditures = new ArrayList<Expenditure>();
        this.id = id;
    }

    public Category(){
        this.name = "";
        this.color = "";
        expenditures = new ArrayList<Expenditure>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSumCategory() {
        sumCategory = 0;
        for(Expenditure expenditure: expenditures){
            sumCategory += expenditure.getAmount();
        }
        return sumCategory;
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
