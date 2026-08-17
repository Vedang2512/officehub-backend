package com.officehub.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequestDTO {


    private String name;


    private String description;


    private Long departmentId;


    private Long managerId;
}