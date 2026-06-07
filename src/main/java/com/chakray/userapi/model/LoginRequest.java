package com.chakray.userapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {

    @JsonProperty("tax_id")
    @NotBlank(message = "tax_id is required")
    @Pattern(
            regexp = "^[A-ZÑ&]{4}\\d{6}[A-Z0-9]{3}$",
            message = "tax_id must have a valid RFC format"
    )
    private String taxId;

    @NotBlank(message = "password is required")
    private String password;

    public LoginRequest() {
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}