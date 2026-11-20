package com.foodhub.repository;

import com.foodhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository
 * Spring Data JPA Repository for User entity.
 * Demonstrates: Generic repository pattern, custom derived finder queries, customer searching & filtering.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByRole(User.Role role);
    List<User> findByRoleOrderByCreatedAtDesc(User.Role role);
    List<User> findByRoleAndStatusOrderByCreatedAtDesc(User.Role role, User.Status status);

    @Query("SELECT u FROM User u WHERE u.role = :role AND (" +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "u.phone LIKE CONCAT('%', :keyword, '%')) ORDER BY u.createdAt DESC")
    List<User> searchCustomers(@Param("role") User.Role role, @Param("keyword") String keyword);
}
