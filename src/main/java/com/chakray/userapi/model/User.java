package com.chakray.userapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class User {

    private UUID id;

    @NotBlank(message = "email is required")
    @Email(message = "email format is invalid")
    private String email;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "phone is required")
    @Pattern(
            regexp = "^(?:\\+\\d{1,3}[ -]?)?(?:\\d[ -]?){9}\\d$",
            message = "phone must contain 10 digits and may include a country code"
    )
    private String phone;

   @NotBlank(message = "password is required")
   @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    @JsonProperty("tax_id")
    @NotBlank(message = "tax_id is required")
    @Pattern(
            regexp = "^[A-ZÑ&]{4}\\d{6}[A-Z0-9]{3}$",
            message = "tax_id must have a valid RFC format"
    )
    private String taxId;

    @JsonProperty("created_at")
    private String createdAt;
    private List<Address> addresses;

    public User() {
    }

    public User(
            UUID id,
            String email,
            String name,
            String phone,
            String password,
            String taxId,
            String createdAt,
            List<Address> addresses
    ) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.taxId = taxId;
        this.createdAt = createdAt;
        this.addresses = addresses;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}