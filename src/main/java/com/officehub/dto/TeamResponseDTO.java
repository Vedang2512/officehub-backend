package com.officehub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamResponseDTO {


    private Long id;


    private String name;


    private String description;


    private Long organizationId;


    private Long departmentId;


    private String managerName;


    private Long managerId;


    private LocalDateTime createdAt;
}