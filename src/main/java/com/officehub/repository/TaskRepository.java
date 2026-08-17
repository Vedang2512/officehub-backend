package com.officehub.repository;

import java.util.List;

import com.officehub.dto.dashboard.TaskStatusCountDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.officehub.entity.Organization;
import com.officehub.entity.Task;
import com.officehub.entity.TaskPriority;
import com.officehub.entity.User;
import com.officehub.entity.TaskStatus;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedTo(User assignedTo);

    List<Task> findByOrganization(Organization organization);

    List<Task> findByAssignedToAndOrganization(User assignedTo, Organization organization);
    
    List<Task> findTop5ByOrganizationIdOrderByCreatedAtDesc(Long organizationId);
    
    long countByOrganizationId(Long organizationId);


    long countByOrganizationIdAndStatus(Long organizationId, TaskStatus status);


    long countByAssignedToId(Long userId);


    long countByAssignedToIdAndStatus(Long userId, TaskStatus status);
    
    long countByTeam_Id(Long teamId);
    
    long countByTeam_IdAndStatus(Long teamId, TaskStatus status);
    
    long countByTeam_IdAndPriority(Long teamId, TaskPriority priority);
    
    void deleteByAssignedTo(User user);

    void deleteByAssignedBy(User user);
    
    @Query("""
    	       SELECT COUNT(t)
    	       FROM Task t
    	       WHERE t.team.manager.id = :managerId
    	       """)
    	long countTasksByManager(@Param("managerId") Long managerId);
    
    @Query("""
    	       SELECT COUNT(t)
    	       FROM Task t
    	       WHERE t.team.manager.id = :managerId
    	         AND t.dueDate < CURRENT_TIMESTAMP
    	         AND t.status <> :status
    	       """)
    	long countOverdueTasksByManager(
    	        @Param("managerId") Long managerId,
    	        @Param("status") TaskStatus status);
    
    @Query("""
    	       SELECT COUNT(t)
    	       FROM Task t
    	       WHERE t.team.manager.id = :managerId
    	         AND t.status = :status
    	       """)
    	long countTasksByManagerAndStatus(
    	        @Param("managerId") Long managerId,
    	        @Param("status") TaskStatus status);
    
    @Query("""
    	       SELECT COUNT(t)
    	       FROM Task t
    	       WHERE t.team.department.id = :departmentId
    	       """)
    	long countTasksByDepartment(@Param("departmentId") Long departmentId);
    
    @Query("""
    	       SELECT COUNT(t)
    	       FROM Task t
    	       WHERE t.team.department.id = :departmentId
    	         AND t.status = :status
    	       """)
    	long countTasksByDepartmentAndStatus(
    	        @Param("departmentId") Long departmentId,
    	        @Param("status") TaskStatus status);
    
    @Query("""
    	       SELECT COUNT(t)
    	       FROM Task t
    	       WHERE t.team.department.id = :departmentId
    	       """)
    	long countByDepartmentId(@Param("departmentId") Long departmentId);
    
    @Query("""
    	       SELECT COUNT(t)
    	       FROM Task t
    	       WHERE t.team.department.id = :departmentId
    	       AND t.status = com.officehub.entity.TaskStatus.COMPLETED
    	       """)
    	long countCompletedByDepartmentId(@Param("departmentId") Long departmentId);
    
    @Query("""
    	       SELECT COUNT(t)
    	       FROM Task t
    	       WHERE t.team.department.id = :departmentId
    	       AND t.status = :status
    	       """)
    	long countByDepartmentIdAndStatus(
    	        @Param("departmentId") Long departmentId,
    	        @Param("status") TaskStatus status);
    
    @Query("""
    	       SELECT new com.officehub.dto.dashboard.TaskStatusCountDTO(
    	           t.status,
    	           COUNT(t)
    	       )
    	       FROM Task t
    	       WHERE t.organization.id = :organizationId
    	       GROUP BY t.status
    	       ORDER BY t.status
    	       """)
    	List<TaskStatusCountDTO> getTaskStatusCountsByOrganization(
    	        @Param("organizationId") Long organizationId);
    
    void deleteByTeamId(Long teamId);
    
    List<Task> findByOrganizationIdOrderByTaskNumberAsc(Long organizationId);
}