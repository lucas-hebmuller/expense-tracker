package com.expensetracker.dto;

import java.math.BigDecimal;

public class CategorySummaryDTO {

    private Long categoryId;
    private String categoryName;
    private BigDecimal totalAmount;
    private Integer transactionCount;

    public CategorySummaryDTO() {}

    public CategorySummaryDTO(Long categoryId, String categoryName,
                              BigDecimal totalAmount, Long transactionCount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        this.transactionCount = transactionCount != null ? transactionCount.intValue() : 0;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }
}
