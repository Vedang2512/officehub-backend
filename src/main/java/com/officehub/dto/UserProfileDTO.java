package com.officehub.dto;

import com.officehub.entity.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDTO {

    private Long id;

    private String fullName;

    private String email;

    private Role role;

    private String profileImage;

    private Long organizationId;

    public UserProfileDTO() {
    }

    public UserProfileDTO(
            Long id,
            String fullName,
            String email,
            Role role,
            String profileImage,
            Long organizationId
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.profileImage = profileImage;
        this.organizationId = organizationId;
    }
}