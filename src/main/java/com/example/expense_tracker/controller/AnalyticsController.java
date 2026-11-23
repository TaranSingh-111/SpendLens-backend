package com.example.expense_tracker.controller;

import com.example.expense_tracker.dto.analytics.AnalyticsResponse;
import com.example.expense_tracker.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    public AnalyticsResponse getAnalytics(
            @RequestParam
            @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            Authentication authentication
            ){

        return analyticsService.getAnalytics(authentication.getName(), startDate, endDate);
    }
}
