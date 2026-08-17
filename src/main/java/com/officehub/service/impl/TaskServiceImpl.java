package com.officehub.service.impl;

import java.util.List;
import com.officehub.websocket.TaskWebSocketService;
import com.officehub.service.NotificationService;
import com.officehub.dto.UpdateTaskRequestDTO;
import com.officehub.entity.Role;
import com.officehub.entity.Task;
import com.officehub.entity.Team;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.officehub.entity.User;
import com.officehub.dto.CreateTaskRequestDTO;
import com.officehub.dto.TaskEventDTO;
import com.officehub.dto.TaskResponseDTO;
import com.officehub.dto.UpdateTaskStatusRequestDTO;
import com.officehub.repository.TaskRepository;
import com.officehub.repository.TeamRepository;
import com.officehub.repository.UserRepository;
import com.officehub.service.TaskService;
import com.officehub.exception.UnauthorizedTaskAccessException;
import com.officehub.exception.UnauthorizedTeamAccessException;
import com.officehub.exception.ResourceNotFoundException;
import com.officehub.exception.TaskNotFoundException;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final NotificationService notificationService;
    private final TaskWebSocketService taskWebSocketService;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            UserRepository userRepository,
            TeamRepository teamRepository,
            NotificationService notificationService,
            TaskWebSocketService taskWebSocketService
    ) {

        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.notificationService = notificationService;
        this.taskWebSocketService = taskWebSocketService;
    }


    @Override
    public TaskResponseDTO createTask(
            CreateTaskRequestDTO request,
            String creatorEmail
    ) {

        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> 
                    new RuntimeException("Creator not found")
                );


        User employee = userRepository.findById(request.getAssignedToUserId())
                .orElseThrow(() ->
                    new TaskNotFoundException("Assigned employee not found")
                );
        
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() ->
                    new ResourceNotFoundException("Team not found")
                );


        if (!creator.getOrganization()
                .getId()
                .equals(employee.getOrganization().getId())) {

            throw new RuntimeException(
                    "Employee does not belong to your organization"
            );
        }
        
        if (!creator.getOrganization()
                .getId()
                .equals(team.getOrganization().getId())) {

            throw new UnauthorizedTeamAccessException(
                    "Selected team does not belong to your organization");
        }


        Task task = new Task();

        List<Task> existingTasks =
                taskRepository.findByOrganizationIdOrderByTaskNumberAsc(
                        creator.getOrganization().getId()
                );

        int nextTaskNumber = 1;

        for (Task existingTask : existingTasks) {

            if (existingTask.getTaskNumber() == null) {
                continue;
            }

            if (existingTask.getTaskNumber() == nextTaskNumber) {
                nextTaskNumber++;
            } else if (existingTask.getTaskNumber() > nextTaskNumber) {
                break;
            }
        }

        task.setTaskNumber(nextTaskNumber);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        task.setAssignedTo(employee);
        task.setAssignedBy(creator);
        task.setTeam(team);
        task.setOrganization(creator.getOrganization());


        Task savedTask = taskRepository.save(task);

        notificationService.sendNotification(
                employee,
                creator,
                "You have been assigned a new task: " + savedTask.getTitle(),
                "TASK_ASSIGNED"
        );
        
        taskWebSocketService.broadcastTaskEvent(

                TaskEventDTO.builder()
                        .eventType("CREATED")
                        .taskId(savedTask.getId())
                        .organizationId(
                                savedTask.getOrganization().getId()
                        )
                        .build()

        );

        return mapToDTO(savedTask);
    }


    @Override
    public List<TaskResponseDTO> getMyTasks(String employeeEmail) {

        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() ->
                        new TaskNotFoundException("Employee not found")
                );


        return taskRepository.findByAssignedTo(employee)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    @Override
    public List<TaskResponseDTO> getOrganizationTasks(String ownerEmail) {

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() ->
                        new TaskNotFoundException("Owner not found")
                );


        return taskRepository
                .findByOrganization(owner.getOrganization())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    @Override
    public TaskResponseDTO updateTaskStatus(
            Long taskId,
            UpdateTaskStatusRequestDTO request,
            String employeeEmail
    ) {

        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found")
                );


        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task not found")
                );
        
        


        if (!task.getAssignedTo()
                .getId()
                .equals(employee.getId())) {

        	throw new UnauthorizedTaskAccessException(
        	        "You can update only your assigned tasks"
        	);
        }


        task.setStatus(request.getStatus());


        Task updatedTask = taskRepository.save(task);

        notificationService.sendNotification(
                updatedTask.getAssignedBy(),
                employee,
                employee.getFullName()
                        + " changed the status of task '"
                        + updatedTask.getTitle()
                        + "' to "
                        + updatedTask.getStatus(),
                "TASK_UPDATED"
        );
        
        taskWebSocketService.broadcastTaskEvent(

                TaskEventDTO.builder()
                        .eventType("STATUS_CHANGED")
                        .taskId(updatedTask.getId())
                        .organizationId(
                                updatedTask.getOrganization().getId()
                        )
                        .build()

        );
        
        taskWebSocketService.broadcastTaskEvent(

                TaskEventDTO.builder()
                        .eventType("UPDATED")
                        .taskId(updatedTask.getId())
                        .organizationId(
                                updatedTask.getOrganization().getId()
                        )
                        .build()

        );

        return mapToDTO(updatedTask);
    }
    
    @Override
    public TaskResponseDTO getTaskById(Long taskId, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task not found")
                );

        switch (user.getRole()) {

            case OWNER:
            case MANAGER:

                if (!task.getOrganization().getId()
                        .equals(user.getOrganization().getId())) {

                    throw new UnauthorizedTaskAccessException(
                            "You are not authorized to view this task"
                    );
                }

                break;

            case EMPLOYEE:

                if (!task.getAssignedTo().getId()
                        .equals(user.getId())) {

                    throw new UnauthorizedTaskAccessException(
                            "You are not authorized to view this task"
                    );
                }

                break;

            default:
                throw new UnauthorizedTaskAccessException(
                        "Unauthorized access"
                );
        }

        return mapToDTO(task);
    }
    
    @Override
    public TaskResponseDTO updateTask(
            Long taskId,
            UpdateTaskRequestDTO request,
            String userEmail
    ) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        if (user.getRole() != com.officehub.entity.Role.OWNER
                && user.getRole() != com.officehub.entity.Role.MANAGER) {

            throw new UnauthorizedTaskAccessException(
                    "Only Owner or Manager can edit tasks"
            );
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task not found")
                );
        
        if (user.getRole() == Role.MANAGER
                && task.getAssignedTo().getRole() != Role.EMPLOYEE) {

            throw new UnauthorizedTaskAccessException(
                    "Managers can only edit tasks assigned to employees."
            );
        }
        User previousAssignee = task.getAssignedTo();

        if (!task.getOrganization().getId()
                .equals(user.getOrganization().getId())) {

            throw new UnauthorizedTaskAccessException(
                    "You are not authorized to edit this task"
            );
        }

        User employee = userRepository.findById(request.getAssignedToUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Assigned employee not found")
                );

        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Team not found")
                );

        if (!employee.getOrganization().getId()
                .equals(user.getOrganization().getId())) {

            throw new UnauthorizedTaskAccessException(
                    "Employee does not belong to your organization"
            );
        }

        if (!team.getOrganization().getId()
                .equals(user.getOrganization().getId())) {

            throw new UnauthorizedTeamAccessException(
                    "Team does not belong to your organization"
            );
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
        task.setAssignedTo(employee);
        task.setTeam(team);

        Task updatedTask = taskRepository.save(task);
        
        taskWebSocketService.broadcastTaskEvent(
                TaskEventDTO.builder()
                        .eventType("UPDATED")
                        .taskId(updatedTask.getId())
                        .organizationId(
                                updatedTask.getOrganization().getId()
                        )
                        .build()
        );

        if (!previousAssignee.getId().equals(employee.getId())) {

        	notificationService.sendNotification(
        	        employee,
        	        user,
        	        "You have been assigned a new task: "
        	                + updatedTask.getTitle(),
        	        "TASK_ASSIGNED"
        	);
        }

        return mapToDTO(updatedTask);
    }
    
    @Override
    public void deleteTask(Long taskId, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task not found")
                );
        
        if (user.getRole() == Role.MANAGER
                && task.getAssignedTo().getRole() != Role.EMPLOYEE) {

            throw new UnauthorizedTaskAccessException(
                    "Managers can only delete tasks assigned to employees."
            );
        }

        if (!task.getOrganization().getId()
                .equals(user.getOrganization().getId())) {

            throw new UnauthorizedTaskAccessException(
                    "You are not authorized to delete this task"
            );
        }
        Long organizationId = task.getOrganization().getId();
        taskRepository.delete(task);
        

        taskWebSocketService.broadcastTaskEvent(

                TaskEventDTO.builder()
                        .eventType("DELETED")
                        .taskId(taskId)
                        .organizationId(organizationId)
                        .build()

        );
    }
    
    private TaskResponseDTO mapToDTO(Task task) {

        TaskResponseDTO dto = new TaskResponseDTO();

        dto.setId(task.getId());
        dto.setTaskNumber(task.getTaskNumber());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());

        dto.setAssignedToUserId(
                task.getAssignedTo().getId()
        );

        dto.setAssignedToName(
                task.getAssignedTo().getFullName()
        );

        dto.setAssignedByUserId(
                task.getAssignedBy().getId()
        );

        dto.setAssignedByName(
                task.getAssignedBy().getFullName()
        );
        dto.setTeamId(task.getTeam().getId());
        dto.setTeamName(task.getTeam().getName());

        dto.setCreatedAt(task.getCreatedAt());
        dto.setDueDate(task.getDueDate());
        
        

        return dto;
    }
}