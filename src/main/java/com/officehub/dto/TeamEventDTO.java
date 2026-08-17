package com.officehub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamEventDTO {

    private String eventType;

    private Long teamId;

    private Long organizationId;
}