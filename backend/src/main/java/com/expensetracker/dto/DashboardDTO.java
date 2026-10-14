package com.expensetracker.dto;

import java.math.BigDecimal;

public class DashboardDTO {

    private MonthlySummaryDTO currentMonth;
    private MonthlySummaryDTO lastMonth;
    private CategorySummaryDTO topCategory;
    private Integer transactionCountThisMonth;
    private BigDecimal totalBalance;

    public DashboardDTO() {}

    public DashboardDTO(MonthlySummaryDTO currentMonth, MonthlySummaryDTO lastMonth,
                        CategorySummaryDTO topCategory, Integer transactionCountThisMonth,
                        BigDecimal totalBalance) {
        this.currentMonth = currentMonth;
        this.lastMonth = lastMonth;
        this.topCategory = topCategory;
        this.transactionCountThisMonth = transactionCountThisMonth;
        this.totalBalance = totalBalance;
    }

    public MonthlySummaryDTO getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(MonthlySummaryDTO currentMonth) {
        this.currentMonth = currentMonth;
    }

    public MonthlySummaryDTO getLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(MonthlySummaryDTO lastMonth) {
        this.lastMonth = lastMonth;
    }

    public CategorySummaryDTO getTopCategory() {
        return topCategory;
    }

    public void setTopCategory(CategorySummaryDTO topCategory) {
        this.topCategory = topCategory;
    }

    public Integer getTransactionCountThisMonth() {
        return transactionCountThisMonth;
    }

    public void setTransactionCountThisMonth(Integer transactionCountThisMonth) {
        this.transactionCountThisMonth = transactionCountThisMonth;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }
}
