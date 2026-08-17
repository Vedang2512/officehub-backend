package com.officehub.dto;

public class OrganizationMemberDTO {

    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String designation;
    private String profileImage;

    public OrganizationMemberDTO() {
    }

    public OrganizationMemberDTO(
            Long id,
            String fullName,
            String email,
            String role,
            String designation,
            String profileImage) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.designation = designation;
        this.profileImage = profileImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}