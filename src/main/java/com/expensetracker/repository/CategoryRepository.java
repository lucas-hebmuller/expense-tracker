package com.expensetracker.repository;

import com.expensetracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser_Id(Long userId); // SELECT * FROM categories WHERE user_id = ?

    Optional<Category> findByNameAndUser_Id(String name, Long userId); // SELECT * FROM categories WHERE name = ?
                                                                       // AND user_id = ?
}
