package com.officehub.repository;

import java.util.List;

import com.officehub.entity.Role;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.officehub.entity.Organization;
import com.officehub.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByOrganization(Organization organization);
    
    long countByOrganizationIdAndRole(Long organizationId, Role role);


    List<User> findByOrganizationId(Long organizationId);
}