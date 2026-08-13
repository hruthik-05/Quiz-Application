package com.projectquiz.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectquiz.demo.models.AdminAnalyticsDto;
import com.projectquiz.demo.models.ContestAttempt;
import com.projectquiz.demo.models.ContestAttemptDto;
import com.projectquiz.demo.models.UserPerformanceStats;
import com.projectquiz.demo.models.UserPerformanceStatsDto;
import com.projectquiz.demo.services.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AnalyticsController {
    
    @Autowired
    AnalyticsService analyticsService;

    @GetMapping("/{userId}")
    public UserPerformanceStats getUserStats(@PathVariable String userId) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        com.projectquiz.demo.security.services.UserDetailsImpl userDetails = (com.projectquiz.demo.security.services.UserDetailsImpl) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !userDetails.getId().equals(userId)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Access denied");
        }
        return analyticsService.getUserStats(userId);
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminAnalyticsDto getAdminStats() {
        return analyticsService.getAdminStats();
    }

    @GetMapping("/admin/all-user-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserPerformanceStatsDto> getAllUserStats() {
        return analyticsService.getAllUserStats();
    }
    
    @GetMapping("/admin/all-contest-attempts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ContestAttemptDto> getAllContestAttempts() {
        return analyticsService.getAllContestAttempts();
    }
}
