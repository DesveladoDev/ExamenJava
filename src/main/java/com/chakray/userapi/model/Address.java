package com.chakray.userapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {

    private Long id;
    private String name;
    private String street;
    @JsonProperty("country_code")
    private String countryCode;

    public Address() {
    }

    public Address(Long id, String name, String street, String countryCode) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.countryCode = countryCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}