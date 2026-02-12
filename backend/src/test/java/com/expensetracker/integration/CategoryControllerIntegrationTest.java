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
    void createCategory_WithoutAuth_ShouldReturn403() throws Exception {
        String requestBody = """
                {
                    "name": "Groceries"
                }
                """;

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCategories_WithValidToken_ShouldReturnUserCategories() throws Exception {
        // Create categories for test user
        Category cat1 = new Category();
        cat1.setName("Groceries");
        cat1.setUser(testUser);
        categoryRepository.save(cat1);

        Category cat2 = new Category();
        cat2.setName("Transport");
        cat2.setUser(testUser);
        categoryRepository.save(cat2);

        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Groceries"))
                .andExpect(jsonPath("$[1].name").value("Transport"));
    }

    @Test
    void getCategories_ShouldNotReturnOtherUsersCategories() throws Exception {
        // Create category for test user
        Category myCategory = new Category();
        myCategory.setName("My Category");
        myCategory.setUser(testUser);
        categoryRepository.save(myCategory);

        // Create another user with their own category
        User otherUser = createTestUser("other@example.com");
        Category otherCategory = new Category();
        otherCategory.setName("Other Category");
        otherCategory.setUser(otherUser);
        categoryRepository.save(otherCategory);

        // Should only see my category
        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("My Category"));
    }

    @Test
    void updateCategory_WithValidData_ShouldReturn200() throws Exception {
        Category category = new Category();
        category.setName("Groceries");
        category.setUser(testUser);
        category = categoryRepository.save(category);

        String requestBody = """
                {
                    "name": "Food & Groceries"
                }
                """;

        mockMvc.perform(put("/api/categories/" + category.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Food & Groceries"));
    }

    @Test
    void updateCategory_OtherUsersCategory_ShouldReturn404() throws Exception {
        // Create another user's category
        User otherUser = createTestUser("other@example.com");
        Category otherCategory = new Category();
        otherCategory.setName("Other Category");
        otherCategory.setUser(otherUser);
        otherCategory = categoryRepository.save(otherCategory);

        String requestBody = """
                {
                    "name": "Hacked!"
                }
                """;

        mockMvc.perform(put("/api/categories/" + otherCategory.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_WithNoTransactions_ShouldReturn204() throws Exception {
        Category category = new Category();
        category.setName("Groceries");
        category.setUser(testUser);
        category = categoryRepository.save(category);

        mockMvc.perform(delete("/api/categories/" + category.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_OtherUsersCategory_ShouldReturn404() throws Exception {
        User otherUser = createTestUser("other@example.com");
        Category otherCategory = new Category();
        otherCategory.setName("Other Category");
        otherCategory.setUser(otherUser);
        otherCategory = categoryRepository.save(otherCategory);

        mockMvc.perform(delete("/api/categories/" + otherCategory.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
