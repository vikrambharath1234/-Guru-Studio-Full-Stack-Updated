package com.example.demo.controller;

import com.example.demo.dto.DashboardSummaryResponse;
import com.example.demo.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Admin only (see SecurityConfig). GET /api/admin/summary */
@RestController
@RequestMapping("/api/admin/summary")
public class AdminDashboardController {

	private final DashboardService dashboardService;

	public AdminDashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping
	public DashboardSummaryResponse getSummary() {
		return dashboardService.getSummary();
	}
}
