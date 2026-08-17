package com.officehub.service;

import java.util.List;

import com.officehub.dto.CreateOrganizationRequestDTO;
import com.officehub.dto.OrganizationMemberDTO;
import com.officehub.dto.OrganizationResponseDTO;
import com.officehub.dto.UpdateOrganizationRequestDTO;

public interface OrganizationService {

    OrganizationResponseDTO createOrganization(
            CreateOrganizationRequestDTO request,
            String email);

    OrganizationResponseDTO getMyOrganization(
            String email);

    void leaveOrganization(
            String email);
    
    void deleteOrganization(String email);

    List<OrganizationMemberDTO> getOrganizationMembers(
            String email);

    OrganizationResponseDTO updateOrganization(
            UpdateOrganizationRequestDTO request,
            String email);
}