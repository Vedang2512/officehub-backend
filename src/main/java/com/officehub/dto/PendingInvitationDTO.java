package com.officehub.dto;

public class PendingInvitationDTO {

    private Long id;
    private String organizationName;
    private String description;

    public PendingInvitationDTO() {
    }

    public PendingInvitationDTO(
            Long id,
            String organizationName,
            String description) {

        this.id = id;
        this.organizationName = organizationName;
        this.description = description;
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
}