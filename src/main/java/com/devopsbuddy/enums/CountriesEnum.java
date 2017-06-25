package com.devopsbuddy.enums;

/**
 * Created by root on 16/06/17.
 */
public enum CountriesEnum {

    BULGARIA("BG", "Bulgaria"),
    BRAZIL("BR", "Brazil"),
    CHINA("CN", "China"),
    CZECH_REPUBLIC("CZ", "Czech Republic"),
    DENMARK("DK", "Denmark"),
    FRANCE("FK", "France"),
    GERMANY("DE", "Germany"),
    INDIA("IN", "India"),
    MOROCCO("MA", "Morocco"),
    NETHERLANDS("NL", "Netherlands"),
    PAKISTAN("PK", "Pakistan"),
    ROMANIA("RO", "Romania"),
    RUSSIA("RU", "Russia"),
    SLOVAKIA("SK", "Slovakia"),
    SPAIN("ES", "Spain"),
    THAILAND("TH", "Thailand"),
    UNITED_ARAB_EMIRATES("AE", "United Arab Emirates"),
    UNITED_KINGDOM("GB", "United Kingdom"),
    UNITED_STATES("US", "United States"),
    VENEZUELA("VE", "Venezuela");

    private final String code;

    private final String name;

    CountriesEnum(String code, String country) {
        this.code = code;
        this.name = country;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
