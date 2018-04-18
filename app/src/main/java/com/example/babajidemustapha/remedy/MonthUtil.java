package com.example.babajidemustapha.remedy;

import java.util.HashMap;

/**
 * Created by Jide Mustapha on 4/11/2018.
 */

public class MonthUtil {
    private static final HashMap<String, Integer> months = new HashMap<>();

    static {
        months.put("January", 1);
        months.put("February", 2);
        months.put("March", 3);
        months.put("April", 4);
        months.put("May", 5);
        months.put("June", 6);
        months.put("July", 7);
        months.put("August", 8);
        months.put("September", 9);
        months.put("October", 10);
        months.put("November", 11);
        months.put("December", 12);
    }

    public static int getMonthNumber(String month) {
        return months.get(month);
    }
}
