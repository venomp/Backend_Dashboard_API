package com.finance.dashboard.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.finance.dashboard.dto.CreateRecordRequest;
import com.finance.dashboard.dto.DashboardResponse;
import com.finance.dashboard.entity.FinancialRecordEntity;
import com.finance.dashboard.entity.TransactionType;
import com.finance.dashboard.entity.UserEntity;
import com.finance.dashboard.entity.UserRole;
import com.finance.dashboard.exception.ApiException;
import com.finance.dashboard.repository.FinancialRecordRepository;

@Service
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository repository;

    public FinancialRecordServiceImpl(FinancialRecordRepository repository) {
        this.repository = repository;
    }

    private UserEntity getCurrentUser() {
        return (UserEntity) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @Override
    public FinancialRecordEntity createRecord(CreateRecordRequest request) {

        UserEntity user = getCurrentUser();

        if (user.getRole() != UserRole.ADMIN) {
            
            throw new ApiException("You are not allowed to create records");
        }

        FinancialRecordEntity record = new FinancialRecordEntity();
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setTransactionDate(request.getTransactionDate());
        record.setDescription(request.getDescription());
        record.setUser(user);

        return repository.save(record);
    }

    @Override
    public List<FinancialRecordEntity> getUserRecords() {

        UserEntity user = getCurrentUser();

        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.ANALYST) {
            return repository.findAll();
        }

        return repository.findByUserId(user.getId());
    }

    @Override
    public List<FinancialRecordEntity> getRecordsByType(TransactionType type) {

        UserEntity user = getCurrentUser();

        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.ANALYST) {
            return repository.findAll()
                    .stream()
                    .filter(r -> r.getType() == type)
                    .toList();
        }

        return repository.findByUserIdAndType(user.getId(), type);
    }

    @Override
    public List<FinancialRecordEntity> getRecordsByDateRange(LocalDateTime start, LocalDateTime end) {

        UserEntity user = getCurrentUser();

        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.ANALYST) {
            return repository.findAll()
                    .stream()
                    .filter(r -> !r.getTransactionDate().isBefore(start)
                            && !r.getTransactionDate().isAfter(end))
                    .toList();
        }

        return repository.findByUserIdAndTransactionDateBetween(user.getId(), start, end);
    }

    @Override
    public DashboardResponse getDashboardSummary() {

        UserEntity user = getCurrentUser();

        BigDecimal income;
        BigDecimal expense;
        List<Object[]> raw;

        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.ANALYST) {

            List<FinancialRecordEntity> allRecords = repository.findAll();

            income = allRecords.stream()
                    .filter(r -> r.getType() == TransactionType.INCOME)
                    .map(FinancialRecordEntity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            expense = allRecords.stream()
                    .filter(r -> r.getType() == TransactionType.EXPENSE)
                    .map(FinancialRecordEntity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            raw = allRecords.stream()
                    .collect(
                            java.util.stream.Collectors.groupingBy(
                                    FinancialRecordEntity::getCategory,
                                    java.util.stream.Collectors.reducing(
                                            BigDecimal.ZERO,
                                            FinancialRecordEntity::getAmount,
                                            BigDecimal::add)))
                    .entrySet()
                    .stream()
                    .map(e -> new Object[]{e.getKey(), e.getValue()})
                    .toList();

        } else {

            income = repository.getTotalByType(user.getId(), TransactionType.INCOME);
            expense = repository.getTotalByType(user.getId(), TransactionType.EXPENSE);
            raw = repository.getCategorySummary(user.getId());
        }

        Map<String, BigDecimal> categoryMap = new HashMap<>();

        for (Object[] row : raw) {
            categoryMap.put((String) row[0], (BigDecimal) row[1]);
        }

        DashboardResponse response = new DashboardResponse();
        response.setTotalIncome(income);
        response.setTotalExpense(expense);
        response.setNetBalance(income.subtract(expense));
        response.setCategorySummary(categoryMap);

        return response;
    }
}

