package com.finance.dashboard.dto;

import java.math.BigDecimal;
import java.util.Map;

public class DashboardResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> categorySummary;

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public Map<String, BigDecimal> getCategorySummary() {
        return categorySummary;
    }

    public void setCategorySummary(Map<String, BigDecimal> categorySummary) {
        this.categorySummary = categorySummary;
    }
}

