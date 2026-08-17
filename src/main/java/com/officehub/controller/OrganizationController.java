package com.officehub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import com.officehub.dto.UpdateOrganizationRequestDTO;
import com.officehub.dto.OrganizationMemberDTO;
import com.officehub.dto.CreateOrganizationRequestDTO;
import com.officehub.dto.OrganizationResponseDTO;
import com.officehub.service.OrganizationService;
import org.springframework.security.core.Authentication;
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }


    @PostMapping
    public ResponseEntity<OrganizationResponseDTO> createOrganization(
            @RequestBody CreateOrganizationRequestDTO request,
            Authentication authentication) {

        OrganizationResponseDTO response =
                organizationService.createOrganization(request, authentication.getName());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/me")
    public ResponseEntity<OrganizationResponseDTO> getMyOrganization(
            Authentication authentication) {

        OrganizationResponseDTO response =
                organizationService.getMyOrganization(
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/leave")
    public ResponseEntity<Void> leaveOrganization(
            Authentication authentication) {

        organizationService.leaveOrganization(
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/members")
    public ResponseEntity<List<OrganizationMemberDTO>> getOrganizationMembers(
            Authentication authentication) {

        List<OrganizationMemberDTO> members =
                organizationService.getOrganizationMembers(
                        authentication.getName()
                );

        return ResponseEntity.ok(members);
    }
    
    @PutMapping
    public ResponseEntity<OrganizationResponseDTO> updateOrganization(
            @RequestBody UpdateOrganizationRequestDTO request,
            Authentication authentication) {

        OrganizationResponseDTO response =
                organizationService.updateOrganization(
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteOrganization(
            Authentication authentication) {

        organizationService.deleteOrganization(
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}