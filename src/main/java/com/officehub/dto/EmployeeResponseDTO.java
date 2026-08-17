package com.officehub.dto;

import com.officehub.entity.Role;

public class EmployeeResponseDTO {

	private Long id;
	private String name;
	private String email;
	private Role role;

	private String profileImage;
	private String designation;


	public EmployeeResponseDTO(
	        Long id,
	        String name,
	        String email,
	        Role role,
	        String profileImage,
	        String designation
	) {
	    this.id = id;
	    this.name = name;
	    this.email = email;
	    this.role = role;
	    this.profileImage = profileImage;
	    this.designation = designation;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}