package com.papramaki.papramaki.utils;

import com.papramaki.papramaki.models.Budget;
import com.papramaki.papramaki.models.Category;
import com.papramaki.papramaki.models.History;

public class LocalData {

    public static History history = new History();
    public static Budget budget = new Budget(0,0);
    public static Category category = new Category("","");
    public static int userId = 0;
    public static double balance = 0;
}
