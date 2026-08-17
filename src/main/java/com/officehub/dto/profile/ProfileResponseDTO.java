package com.officehub.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {

    private Long id;

    private String fullName;

    private String email;

    private String role;

    private String organizationName;

    private String phoneNumber;

    private String designation;

    private String profileImage;

}