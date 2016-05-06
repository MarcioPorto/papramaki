package com.papramaki.papramaki.utils;

import com.papramaki.papramaki.models.Budget;
import com.papramaki.papramaki.models.Category;
import com.papramaki.papramaki.models.History;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a halper class that temporarily stores data retrieved from the API.
 */
public class LocalData {

    public static History history = new History();
    public static Budget budget = new Budget(0,0);
    public static List<Category> categories = new ArrayList<>();
    public static double balance = 0;

}
