package com.expensetracker.dto;

public class DashboardDTO {

    private MonthlySummaryDTO currentMonth;
    private MonthlySummaryDTO lastMonth;
    private CategorySummaryDTO topCategory;
    private Integer transactionCountThisMonth;

    public DashboardDTO() {}

    public DashboardDTO(MonthlySummaryDTO currentMonth, MonthlySummaryDTO lastMonth,
                        CategorySummaryDTO topCategory, Integer transactionCountThisMonth) {
        this.currentMonth = currentMonth;
        this.lastMonth = lastMonth;
        this.topCategory = topCategory;
        this.transactionCountThisMonth = transactionCountThisMonth;
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
}
