package com.officehub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponseDTO {

    private Long id;

    private String name;

    private String description;

    private Long organizationId;

    private LocalDateTime createdAt;
}