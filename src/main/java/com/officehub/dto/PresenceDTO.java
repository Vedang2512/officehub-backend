package com.officehub.dto;

public class PresenceDTO {

    private Long userId;

    private String status;


    public PresenceDTO() {
    }


    public PresenceDTO(Long userId, String status) {
        this.userId = userId;
        this.status = status;
    }


    public Long getUserId() {
        return userId;
    }


    public String getStatus() {
        return status;
    }

}