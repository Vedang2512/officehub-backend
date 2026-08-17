package com.officehub.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocketUser {

    private Long userId;

    private Long organizationId;

    private String role;

    private String email;
}