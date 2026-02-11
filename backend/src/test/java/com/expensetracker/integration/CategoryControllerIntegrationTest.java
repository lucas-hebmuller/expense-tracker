package com.expensetracker.integration;

import com.expensetracker.model.Category;
import com.expensetracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CategoryControllerIntegrationTest extends BaseIntegrationTest {

    private User testUser;
    private String token;

    @BeforeEach
    void setUp() {
        cleanDatabase();
        testUser = createTestUser("test@example.com");
        token = getTokenForUser(testUser);
    }

    @Test
    void createCategory_WithValidData_ShouldReturn201() throws Exception {
        String requestBody = """
                {
                    "name": "Groceries"
                }
                """;

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Groceries"));
    }

    @Test
    void createCategory_WithDuplicateName_ShouldReturn409() throws Exception {
        // Create first category
        Category category = new Category();
        category.setName("Groceries");
        category.setUser(testUser);
        categoryRepository.save(category);

        // Try to create duplicate
        String requestBody = """
                {
                    "name": "Groceries"
                }
                """;

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void createCategory_WithoutAuth_ShouldReturn401() throws Exception {
        String requestBody = """
                {
                    "name": "Groceries"
                }
                """;

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}
