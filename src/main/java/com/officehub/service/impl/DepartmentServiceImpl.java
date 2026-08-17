package com.officehub.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officehub.dto.DepartmentRequestDTO;
import com.officehub.dto.DepartmentResponseDTO;
import com.officehub.entity.Department;
import com.officehub.entity.Organization;
import com.officehub.entity.User;
import com.officehub.exception.ResourceNotFoundException;
import com.officehub.repository.DepartmentRepository;
import com.officehub.repository.OrganizationRepository;
import com.officehub.repository.UserRepository;
import com.officehub.service.DepartmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {


    private final DepartmentRepository departmentRepository;

    private final UserRepository userRepository;

    private final OrganizationRepository organizationRepository;



    @Override
    public DepartmentResponseDTO createDepartment(
            DepartmentRequestDTO request,
            String userEmail
    ) {

        User user = getUser(userEmail);


        Organization organization = organizationRepository
                .findById(user.getOrganization().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organization not found"
                        )
                );


        if(departmentRepository.existsByNameAndOrganizationId(
                request.getName(),
                organization.getId()
        )) {

            throw new RuntimeException(
                    "Department already exists"
            );
        }


        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .organization(organization)
                .build();


        Department saved =
                departmentRepository.save(department);


        return mapToResponse(saved);
    }




    @Override
    public DepartmentResponseDTO updateDepartment(
            Long departmentId,
            DepartmentRequestDTO request,
            String userEmail
    ) {

        User user = getUser(userEmail);


        Department department =
                departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found"
                        )
                );


        if(!department.getOrganization()
                .getId()
                .equals(user.getOrganization().getId())) {

            throw new RuntimeException(
                    "Unauthorized access"
            );
        }


        department.setName(request.getName());
        department.setDescription(request.getDescription());


        return mapToResponse(
                departmentRepository.save(department)
        );
    }




    @Override
    public void deleteDepartment(
            Long departmentId,
            String userEmail
    ) {

        User user = getUser(userEmail);


        Department department =
                departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found"
                        )
                );


        if(!department.getOrganization()
                .getId()
                .equals(user.getOrganization().getId())) {

            throw new RuntimeException(
                    "Unauthorized access"
            );
        }


        departmentRepository.delete(department);
    }




    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getDepartments(
            String userEmail
    ) {

        User user = getUser(userEmail);


        List<Department> departments =
                departmentRepository.findByOrganizationId(
                        user.getOrganization().getId()
                );


        return departments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }




    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }



    private DepartmentResponseDTO mapToResponse(
            Department department
    ) {

        return DepartmentResponseDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .organizationId(
                        department.getOrganization().getId()
                )
                .createdAt(department.getCreatedAt())
                .build();
    }
}