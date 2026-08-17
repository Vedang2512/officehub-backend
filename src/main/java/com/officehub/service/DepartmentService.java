package com.officehub.service;

import java.util.List;

import com.officehub.dto.DepartmentRequestDTO;
import com.officehub.dto.DepartmentResponseDTO;

public interface DepartmentService {

    DepartmentResponseDTO createDepartment(
            DepartmentRequestDTO request,
            String userEmail
    );


    DepartmentResponseDTO updateDepartment(
            Long departmentId,
            DepartmentRequestDTO request,
            String userEmail
    );


    void deleteDepartment(
            Long departmentId,
            String userEmail
    );


    List<DepartmentResponseDTO> getDepartments(
            String userEmail
    );
}