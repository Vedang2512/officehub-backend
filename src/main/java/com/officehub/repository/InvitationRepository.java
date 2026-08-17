package com.officehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.officehub.entity.Invitation;
import com.officehub.entity.InvitationStatus;
import com.officehub.entity.Organization;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findByEmailAndStatus(
            String email,
            InvitationStatus status
    );

    List<Invitation> findByOrganization(Organization organization);

    boolean existsByEmailAndOrganizationAndStatus(
            String email,
            Organization organization,
            InvitationStatus status
    ); 
    void deleteByOrganization(Organization organization);
}