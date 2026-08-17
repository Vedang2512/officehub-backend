package com.officehub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.officehub.dto.DepartmentRequestDTO;
import com.officehub.dto.DepartmentResponseDTO;
import com.officehub.service.DepartmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {


    private final DepartmentService departmentService;



    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> createDepartment(
            @RequestBody DepartmentRequestDTO request,
            Authentication authentication
    ) {

        String email = authentication.getName();


        return new ResponseEntity<>(
                departmentService.createDepartment(
                        request,
                        email
                ),
                HttpStatus.CREATED
        );
    }



    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(
            @PathVariable Long id,
            @RequestBody DepartmentRequestDTO request,
            Authentication authentication
    ) {

        String email = authentication.getName();


        return ResponseEntity.ok(
                departmentService.updateDepartment(
                        id,
                        request,
                        email
                )
        );
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String email = authentication.getName();


        departmentService.deleteDepartment(
                id,
                email
        );


        return ResponseEntity.ok(
                "Department deleted successfully"
        );
    }





    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getDepartments(
            Authentication authentication
    ) {

        String email = authentication.getName();


        return ResponseEntity.ok(
                departmentService.getDepartments(email)
        );
    }
}