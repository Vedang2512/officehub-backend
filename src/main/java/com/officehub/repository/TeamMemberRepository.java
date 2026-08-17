package com.officehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.officehub.entity.TeamMember;
import com.officehub.entity.User;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeamId(Long teamId);

    List<TeamMember> findByUserId(Long userId);

    Optional<TeamMember> findByTeamIdAndUserId(
            Long teamId,
            Long userId
    );

    boolean existsByTeamIdAndUserId(
            Long teamId,
            Long userId
    );

    void deleteByTeamIdAndUserId(
            Long teamId,
            Long userId
    );
    
    @Query("""
    	       SELECT COUNT(tm)
    	       FROM TeamMember tm
    	       WHERE tm.team.manager.id = :managerId
    	       """)
    	long countMembersByManager(@Param("managerId") Long managerId);
    
    @Query("""
    	       SELECT COUNT(tm)
    	       FROM TeamMember tm
    	       WHERE tm.team.department.id = :departmentId
    	       """)
    	long countMembersByDepartment(@Param("departmentId") Long departmentId);
    
    long countByTeam_Id(Long teamId);
    
    void deleteByUser(User user);
    
    @Query("""
    	       SELECT COUNT(tm)
    	       FROM TeamMember tm
    	       WHERE tm.team.department.id = :departmentId
    	       """)
    	long countEmployeesByDepartment(@Param("departmentId") Long departmentId);
}