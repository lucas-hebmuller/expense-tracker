package com.expensetracker.dto;

import java.math.BigDecimal;

public class MonthlySummaryDTO {

    private Integer year;
    private Integer month;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netAmount;
    private Integer transactionCount;

    public MonthlySummaryDTO() {}

    public MonthlySummaryDTO(Integer year, Integer month, BigDecimal totalIncome,
                             BigDecimal totalExpenses, Integer transactionCount) {
        this.year = year;
        this.month = month;
        this.totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        this.totalExpenses = totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
        this.netAmount = this.totalIncome.add(this.totalExpenses);
        this.transactionCount = transactionCount;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }
}
