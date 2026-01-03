package com.expensetracker.repository;

import com.expensetracker.dto.CategorySummaryDTO;
import com.expensetracker.dto.DateRangeSummaryDTO;
import com.expensetracker.dto.MonthlySummaryDTO;
import com.expensetracker.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUser_Id(Long userId, Pageable pageable);

    Page<Transaction> findByCategory_Id(Long categoryId, Pageable pageable);

    @Query("SELECT new com.expensetracker.dto.MonthlySummaryDTO(" +
            "EXTRACT(YEAR FROM t.transactionDate), " +
            "EXTRACT(MONTH FROM t.transactionDate), " +
            "COALESCE(SUM(CASE WHEN t.amount > 0 THEN t.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN t.amount < 0 THEN t.amount ELSE 0 END), 0), " +
            "CAST(COUNT(t) AS integer)) " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND EXTRACT(YEAR FROM t.transactionDate) = :year " +
            "AND EXTRACT(MONTH FROM t.transactionDate) = :month " +
            "GROUP BY EXTRACT(YEAR FROM t.transactionDate), EXTRACT(MONTH FROM t.transactionDate)")
    MonthlySummaryDTO getMonthlySummary(@Param("userId") Long userId,
                                        @Param("year") Integer year,
                                        @Param("month") Integer month);

    @Query("SELECT new com.expensetracker.dto.CategorySummaryDTO(" +
            "c.id, c.name, SUM(t.amount), COUNT(t)) " +
            "FROM Transaction t " +
            "JOIN t.category c " +
            "WHERE t.user.id = :userId " +
            "GROUP BY c.id, c.name " +
            "ORDER BY SUM(t.amount) DESC")
    List<CategorySummaryDTO> getCategorySummary(@Param("userId") Long userId);

    @Query("SELECT new com.expensetracker.dto.DateRangeSummaryDTO(" +
            ":startDate, " +
            ":endDate, " +
            "COALESCE(SUM(CASE WHEN t.amount > 0 THEN t.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN t.amount < 0 THEN t.amount ELSE 0 END), 0), " +
            "COUNT(t)) " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    DateRangeSummaryDTO getDateRangeSummary(@Param("userId") Long userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.expensetracker.dto.CategorySummaryDTO(" +
            "c.id, c.name, SUM(t.amount), COUNT(t)) " +
            "FROM Transaction t " +
            "JOIN t.category c " +
            "WHERE t.user.id = :userId " +
            "AND EXTRACT(YEAR FROM t.transactionDate) = :year " +
            "AND EXTRACT(MONTH FROM t.transactionDate) = :month " +
            "GROUP BY c.id, c.name " +
            "ORDER BY SUM(t.amount) ASC")
    List<CategorySummaryDTO> getCategorySummaryByMonth(@Param("userId") Long userId,
                                                       @Param("year") Integer year,
                                                       @Param("month") Integer month);
}
