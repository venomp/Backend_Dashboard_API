
package com.finance.dashboard.service;

import java.time.LocalDateTime;
import java.util.List;

import com.finance.dashboard.dto.CreateRecordRequest;
import com.finance.dashboard.dto.DashboardResponse;
import com.finance.dashboard.entity.FinancialRecordEntity;
import com.finance.dashboard.entity.TransactionType;

public interface FinancialRecordService {

    FinancialRecordEntity createRecord(CreateRecordRequest request);

    List<FinancialRecordEntity> getUserRecords();

    List<FinancialRecordEntity> getRecordsByType(TransactionType type);

    List<FinancialRecordEntity> getRecordsByDateRange(LocalDateTime start, LocalDateTime end);

    DashboardResponse getDashboardSummary();
}

