package com.officehub.dto;

public class CreateOrganizationRequestDTO {

    private String organizationName;
    private String description;

    public CreateOrganizationRequestDTO() {
    }

    public CreateOrganizationRequestDTO(String organizationName, String description) {
        this.organizationName = organizationName;
        this.description = description;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}