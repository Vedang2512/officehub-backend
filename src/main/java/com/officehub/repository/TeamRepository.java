package com.officehub.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.officehub.entity.Team;
import com.officehub.entity.User;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {


    List<Team> findByOrganizationId(Long organizationId);


    List<Team> findByDepartmentId(Long departmentId);


    List<Team> findByManagerId(Long managerId);


    boolean existsByNameAndOrganizationId(
            String name,
            Long organizationId
    );
    
    long countByManager_Id(Long managerId);
    
    long countByDepartment_Id(Long departmentId);
    
    long countByDepartmentId(Long departmentId);
    
    @Modifying
    @Query("UPDATE Team t SET t.manager = NULL WHERE t.manager = :user")
    void clearManager(@Param("user") User user);
}