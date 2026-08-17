package com.officehub.dto;

import java.time.LocalDateTime;

import com.officehub.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberResponseDTO {

    private Long userId;

    private String fullName;

    private String email;

    private Role role;

    private LocalDateTime joinedAt;
}