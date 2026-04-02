package com.finance.dashboard.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.finance.dashboard.service.FinancialRecordService;
import com.finance.dashboard.entity.FinancialRecordEntity;
import com.finance.dashboard.entity.TransactionType;
import com.finance.dashboard.dto.CreateRecordRequest;
import com.finance.dashboard.dto.DashboardResponse;
import com.finance.dashboard.exception.ApiException;

@RestController
@RequestMapping("/records")
@Validated
public class FinancialRecordController {

    private final FinancialRecordService service;

    public FinancialRecordController(FinancialRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FinancialRecordEntity> createRecord(@Valid @RequestBody CreateRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRecord(request));
    }

    @GetMapping
    public ResponseEntity<List<FinancialRecordEntity>> getRecords() {
        return ResponseEntity.ok(service.getUserRecords());
    }

    @GetMapping("/type")
    public ResponseEntity<List<FinancialRecordEntity>> getByType(@RequestParam String type) {
        TransactionType transactionType;
        try {
            transactionType = TransactionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid transaction type: " + type);
        }
        return ResponseEntity.ok(service.getRecordsByType(transactionType));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<FinancialRecordEntity>> getByDateRange(
            @RequestParam String start,
            @RequestParam String end
    ) {
        try {
            var formatter = DateTimeFormatter.ISO_DATE_TIME;
            var startDate = LocalDateTime.parse(start, formatter);
            var endDate = LocalDateTime.parse(end, formatter);

            if (startDate.isAfter(endDate)) {
                throw new ApiException("Start date must not be after end date");
            }

            return ResponseEntity.ok(service.getRecordsByDateRange(startDate, endDate));
        } catch (Exception e) {
            throw new ApiException("Invalid date format. Use ISO_LOCAL_DATE_TIME: yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(service.getDashboardSummary());
    }
}