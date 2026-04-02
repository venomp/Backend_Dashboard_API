package com.finance.dashboard.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finance.dashboard.entity.FinancialRecordEntity;
import com.finance.dashboard.entity.TransactionType;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecordEntity, Long> {

    List<FinancialRecordEntity> findByUserId(Long userId);

    List<FinancialRecordEntity> findByUserIdAndType(Long userId, TransactionType type);

    List<FinancialRecordEntity> findByUserIdAndTransactionDateBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT COALESCE(SUM(f.amount), 0)
        FROM FinancialRecordEntity f
        WHERE f.user.id = :userId AND f.type = :type
    """)
    BigDecimal getTotalByType(@Param("userId") Long userId, @Param("type") TransactionType type);

    @Query("""
        SELECT f.category, COALESCE(SUM(f.amount), 0)
        FROM FinancialRecordEntity f
        WHERE f.user.id = :userId
        GROUP BY f.category
    """)
    List<Object[]> getCategorySummary(@Param("userId") Long userId);
}

