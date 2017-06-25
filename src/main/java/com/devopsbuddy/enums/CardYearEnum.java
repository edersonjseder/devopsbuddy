package com.devopsbuddy.enums;

/**
 * Created by root on 16/06/17.
 */
public enum CardYearEnum {

    year_2016("2016"),
    year_2017("2017"),
    year_2018("2018"),
    year_2019("2019"),
    year_2020("2020"),
    year_2021("2021"),
    year_2022("2022"),
    year_2023("2023"),
    year_2024("2024"),
    year_2025("2025"),
    year_2026("2026"),
    year_2027("2027"),
    year_2028("2028"),
    year_2029("2029"),
    year_2030("2030");

    private final String cardYear;

    CardYearEnum(String cardYear) {
        this.cardYear = cardYear;
    }

    public String getCardYear() {
        return cardYear;
    }
}
