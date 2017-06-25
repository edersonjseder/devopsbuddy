package com.devopsbuddy.enums;

/**
 * Created by root on 16/06/17.
 */
public enum MonthsEnum {

    JANUARY(1, "January"),
    FEBRUARY(2, "February"),
    MARCH(3, "March"),
    APRIL(4, "April"),
    MAY(5, "May"),
    JUNE(6, "June"),
    JULY(7, "July"),
    AUGUST(8, "August"),
    SEPTEMBER(9, "September"),
    OCTOBER(10, "October"),
    NOVEMBER(11, "November"),
    DECEMBER(12, "December");

    private final int id;

    private final String monthOfYear;

    MonthsEnum(int id, String monthOfYear) {
        this.id = id;
        this.monthOfYear = monthOfYear;
    }

    public int getId() {
        return id;
    }

    public String getMonthOfYear() {
        return monthOfYear;
    }
}
