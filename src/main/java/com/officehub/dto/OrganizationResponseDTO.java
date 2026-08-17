package com.officehub.dto;

import java.time.LocalDateTime;

public class OrganizationResponseDTO {

    private Long id;
    private String organizationName;
    private String description;
    private LocalDateTime createdAt;

    public OrganizationResponseDTO() {
    }

    public OrganizationResponseDTO(Long id, String organizationName, String description,
            LocalDateTime createdAt) {
        this.id = id;
        this.organizationName = organizationName;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}