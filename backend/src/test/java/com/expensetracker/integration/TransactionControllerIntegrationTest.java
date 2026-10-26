package com.expensetracker.integration;

import com.expensetracker.model.Category;
import com.expensetracker.model.Transaction;
import com.expensetracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TransactionControllerIntegrationTest extends BaseIntegrationTest{

    private User testUser;
    private String token;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        cleanDatabase();
        testUser = createTestUser("test@example.com");
        token = getTokenForUser(testUser);

        testCategory = new Category();
        testCategory.setName("Groceries");
        testCategory.setUser(testUser);
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    void createTransaction_WithValidData_ShouldReturn201() throws Exception {
        String requestBody = String.format("""
                {
                    "description": "Weekly groceries",
                    "amount": -75.50,
                    "transactionDate": "%s",
                    "category": { "id": %d }
                }
                """, LocalDate.now(), testCategory.getId());

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Weekly groceries"))
                .andExpect(jsonPath("$.amount").value(-75.50));
    }

    @Test
    void createTransaction_WithOtherUsersCategory_ShouldReturn403() throws Exception {
        // Create another user with their category
        User otherUser = createTestUser("other@example.com");
        Category otherCategory = new Category();
        otherCategory.setName("Other Category");
        otherCategory.setUser(otherUser);
        otherCategory = categoryRepository.save(otherCategory);

        String requestBody = String.format("""
                {
                    "description": "Sneaky transaction",
                    "amount": -100.00,
                    "transactionDate": "%s",
                    "category": { "id": %d }
                }
                """, LocalDate.now(), otherCategory.getId());

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTransaction_WithoutAuth_ShouldReturn403() throws Exception {
        String requestBody = String.format("""
                {
                    "description": "Test",
                    "amount": -50.00,
                    "transactionDate": "%s",
                    "category": { "id": %d }
                }
                """, LocalDate.now(), testCategory.getId());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTransactions_WithValidToken_ShouldReturnUserTransactions() throws Exception {
        // Create transactions
        createTestTransaction("Groceries", new BigDecimal("-50.00"));
        createTestTransaction("More groceries", new BigDecimal("-30.00"));

        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void getTransactions_ShouldNotReturnOtherUsersTransactions() throws Exception {
        // Create transaction for test user
        createTestTransaction("My transaction", new BigDecimal("-50.00"));

        // Create another user with transaction
        User otherUser = createTestUser("other@example.com");
        Category otherCategory = new Category();
        otherCategory.setName("Other Category");
        otherCategory.setUser(otherUser);
        otherCategory = categoryRepository.save(otherCategory);

        Transaction otherTransaction = new Transaction();
        otherTransaction.setDescription("Other transaction");
        otherTransaction.setAmount(new BigDecimal("-100.00"));
        otherTransaction.setTransactionDate(LocalDate.now());
        otherTransaction.setCategory(otherCategory);
        otherTransaction.setUser(otherUser);
        transactionRepository.save(otherTransaction);

        // Should only see my transaction
        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].description").value("My transaction"));
    }

    @Test
    void getTransactions_WithPagination_ShouldWork() throws Exception {
        // Create 5 transactions
        for (int i = 1; i <= 5; i++) {
            createTestTransaction("Transaction " + i, new BigDecimal("-10.00"));
        }

        // Get first page with 2 items
        mockMvc.perform(get("/api/transactions")
                        .param("page", "0")
                        .param("size", "2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void updateTransaction_WithValidData_ShouldReturn200() throws Exception {
        Transaction transaction = createTestTransaction("Old description", new BigDecimal("-50.00"));

        String requestBody = String.format("""
                {
                    "description": "Updated description",
                    "amount": -75.00,
                    "transactionDate": "%s",
                    "category": { "id": %d }
                }
                """, LocalDate.now(), testCategory.getId());

        mockMvc.perform(put("/api/transactions/" + transaction.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.amount").value(-75.00));
    }

    @Test
    void updateTransaction_OtherUsersTransaction_ShouldReturn404() throws Exception {
        // Create another user's transaction
        User otherUser = createTestUser("other@example.com");
        Category otherCategory = new Category();
        otherCategory.setName("Other Category");
        otherCategory.setUser(otherUser);
        otherCategory = categoryRepository.save(otherCategory);

        Transaction otherTransaction = new Transaction();
        otherTransaction.setDescription("Other transaction");
        otherTransaction.setAmount(new BigDecimal("-100.00"));
        otherTransaction.setTransactionDate(LocalDate.now());
        otherTransaction.setCategory(otherCategory);
        otherTransaction.setUser(otherUser);
        otherTransaction = transactionRepository.save(otherTransaction);

        String requestBody = String.format("""
                {
                    "description": "Hacked!",
                    "amount": -999.00,
                    "transactionDate": "%s",
                    "category": { "id": %d }
                }
                """, LocalDate.now(), testCategory.getId());

        mockMvc.perform(put("/api/transactions/" + otherTransaction.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTransaction_WithValidId_ShouldReturn204() throws Exception {
        Transaction transaction = createTestTransaction("To delete", new BigDecimal("-50.00"));

        mockMvc.perform(delete("/api/transactions/" + transaction.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTransaction_OtherUsersTransaction_ShouldReturn404() throws Exception {
        // Create another user's transaction
        User otherUser = createTestUser("other@example.com");
        Category otherCategory = new Category();
        otherCategory.setName("Other Category");
        otherCategory.setUser(otherUser);
        otherCategory = categoryRepository.save(otherCategory);

        Transaction otherTransaction = new Transaction();
        otherTransaction.setDescription("Other transaction");
        otherTransaction.setAmount(new BigDecimal("-100.00"));
        otherTransaction.setTransactionDate(LocalDate.now());
        otherTransaction.setCategory(otherCategory);
        otherTransaction.setUser(otherUser);
        otherTransaction = transactionRepository.save(otherTransaction);

        mockMvc.perform(delete("/api/transactions/" + otherTransaction.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private Transaction createTestTransaction(String description, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setCategory(testCategory);
        transaction.setUser(testUser);
        return transactionRepository.save(transaction);
    }

    // ---------- Filtering: list endpoint ----------

    @Test
    void getTransactions_FilteredByCategory_ShouldReturnOnlyThatCategory() throws Exception {
        Category salary = new Category();
        salary.setName("Salary");
        salary.setUser(testUser);
        salary = categoryRepository.save(salary);

        saveTransaction("Groceries 1", new BigDecimal("-50.00"), testCategory, LocalDate.now());
        saveTransaction("Groceries 2", new BigDecimal("-30.00"), testCategory, LocalDate.now());
        saveTransaction("Paycheck", new BigDecimal("2000.00"), salary, LocalDate.now());

        mockMvc.perform(get("/api/transactions")
                        .param("categoryId", testCategory.getId().toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getTransactions_FilteredByDateRange_ShouldReturnOnlyInRange() throws Exception {
        // This is the 42P18 regression case: a date filter that would have thrown
        // "could not determine data type" before the CAST fix.
        saveTransaction("Old", new BigDecimal("-10.00"), testCategory, LocalDate.now().minusMonths(2));
        saveTransaction("Mid", new BigDecimal("-20.00"), testCategory, LocalDate.now().minusMonths(1));
        saveTransaction("New", new BigDecimal("-30.00"), testCategory, LocalDate.now());

        LocalDate start = LocalDate.now().minusMonths(1).minusDays(3);
        LocalDate end = LocalDate.now().minusMonths(1).plusDays(3);

        mockMvc.perform(get("/api/transactions")
                        .param("startDate", start.toString())
                        .param("endDate", end.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].description").value("Mid"));
    }

    @Test
    void getTransactions_FilteredByCategoryAndDate_ShouldReturnIntersection() throws Exception {
        Category salary = new Category();
        salary.setName("Salary");
        salary.setUser(testUser);
        salary = categoryRepository.save(salary);

        saveTransaction("Match", new BigDecimal("-20.00"), testCategory, LocalDate.now().minusMonths(1));
        saveTransaction("Wrong category", new BigDecimal("500.00"), salary, LocalDate.now().minusMonths(1));
        saveTransaction("Out of range", new BigDecimal("-15.00"), testCategory, LocalDate.now().minusMonths(6));

        LocalDate start = LocalDate.now().minusMonths(1).minusDays(3);
        LocalDate end = LocalDate.now().minusMonths(1).plusDays(3);

        mockMvc.perform(get("/api/transactions")
                        .param("categoryId", testCategory.getId().toString())
                        .param("startDate", start.toString())
                        .param("endDate", end.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].description").value("Match"));
    }

    @Test
    void getTransactions_NoFilters_ShouldReturnAll() throws Exception {
        saveTransaction("One", new BigDecimal("-10.00"), testCategory, LocalDate.now());
        saveTransaction("Two", new BigDecimal("-20.00"), testCategory, LocalDate.now());

        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

// ---------- Filtered net summary endpoint ----------

    @Test
    void getFilteredSummary_WithNoFilters_ShouldReturnNetOverAll() throws Exception {
        saveTransaction("Paycheck", new BigDecimal("1000.00"), testCategory, LocalDate.now());
        saveTransaction("Groceries", new BigDecimal("-250.00"), testCategory, LocalDate.now());

        mockMvc.perform(get("/api/transactions/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(1000.00))
                .andExpect(jsonPath("$.totalExpenses").value(-250.00))
                .andExpect(jsonPath("$.netAmount").value(750.00))
                .andExpect(jsonPath("$.transactionCount").value(2));
    }

    @Test
    void getFilteredSummary_FilteredByCategory_ShouldReturnCategoryNet() throws Exception {
        Category salary = new Category();
        salary.setName("Salary");
        salary.setUser(testUser);
        salary = categoryRepository.save(salary);

        saveTransaction("Groceries", new BigDecimal("-100.00"), testCategory, LocalDate.now());
        saveTransaction("Paycheck", new BigDecimal("500.00"), salary, LocalDate.now());

        mockMvc.perform(get("/api/transactions/summary")
                        .param("categoryId", testCategory.getId().toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netAmount").value(-100.00))
                .andExpect(jsonPath("$.transactionCount").value(1));
    }

    @Test
    void getFilteredSummary_FilteredByDateRange_ShouldReturnRangeNet() throws Exception {
        saveTransaction("Old", new BigDecimal("-10.00"), testCategory, LocalDate.now().minusMonths(2));
        saveTransaction("Mid", new BigDecimal("-20.00"), testCategory, LocalDate.now().minusMonths(1));
        saveTransaction("New", new BigDecimal("-30.00"), testCategory, LocalDate.now());

        LocalDate start = LocalDate.now().minusMonths(1).minusDays(3);
        LocalDate end = LocalDate.now().minusMonths(1).plusDays(3);

        mockMvc.perform(get("/api/transactions/summary")
                        .param("startDate", start.toString())
                        .param("endDate", end.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netAmount").value(-20.00))
                .andExpect(jsonPath("$.transactionCount").value(1));
    }

    @Test
    void getFilteredSummary_WithStartDateAfterEndDate_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/transactions/summary")
                        .param("startDate", LocalDate.now().toString())
                        .param("endDate", LocalDate.now().minusDays(10).toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

// ---------- Helper ----------

    private Transaction saveTransaction(String description, BigDecimal amount,
                                        Category category, LocalDate date) {
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setTransactionDate(date);
        transaction.setCategory(category);
        transaction.setUser(testUser);
        return transactionRepository.save(transaction);
    }
}
