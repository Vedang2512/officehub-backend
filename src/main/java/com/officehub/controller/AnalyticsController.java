package com.officehub.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.officehub.dto.DepartmentAnalyticsDTO;
import com.officehub.dto.ManagerDashboardDTO;
import com.officehub.dto.TeamTaskSummaryDTO;
import com.officehub.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/teams/{teamId}")
    public ResponseEntity<TeamTaskSummaryDTO> getTeamAnalytics(
            @PathVariable Long teamId,
            Principal principal) {

        return ResponseEntity.ok(
                analyticsService.getTeamAnalytics(
                        teamId,
                        principal.getName()));
    }

    @GetMapping("/manager")
    public ResponseEntity<ManagerDashboardDTO> getManagerDashboard(
            Principal principal) {

        return ResponseEntity.ok(
                analyticsService.getManagerDashboard(
                        principal.getName()));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentAnalyticsDTO>> getDepartmentAnalytics(
            Principal principal) {

        return ResponseEntity.ok(
                analyticsService.getDepartmentAnalytics(
                        principal.getName()));
    }
}