package com.officehub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class InviteEmployeeRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email")
    private String email;

    public InviteEmployeeRequestDTO() {
    }

    public InviteEmployeeRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}