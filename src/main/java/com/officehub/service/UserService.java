package com.officehub.service;

public interface UserService {

    void deleteCurrentUser(String email);

    void assignManager(
            Long userId,
            String ownerEmail
    );

}