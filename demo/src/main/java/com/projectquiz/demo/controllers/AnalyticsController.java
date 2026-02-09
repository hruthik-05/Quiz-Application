package com.projectquiz.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectquiz.demo.models.UserPerformanceStats;
import com.projectquiz.demo.services.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*", maxAge = 3600)
public class AnalyticsController {
    
    @Autowired
    AnalyticsService analyticsService;

    @GetMapping("/{userId}")
    public UserPerformanceStats getUserStats(@PathVariable String userId) {
        return analyticsService.getUserStats(userId);
    }

    @GetMapping("/admin/stats")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public com.projectquiz.demo.models.AdminAnalyticsDto getAdminStats() {
        return analyticsService.getAdminStats();
    }

    @GetMapping("/admin/all-user-stats")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public java.util.List<com.projectquiz.demo.models.UserPerformanceStatsDto> getAllUserStats() {
        return analyticsService.getAllUserStats();
    }
    
    @GetMapping("/admin/all-contest-attempts")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public java.util.List<com.projectquiz.demo.models.ContestAttempt> getAllContestAttempts() {
        return analyticsService.getAllContestAttempts();
    }
}
