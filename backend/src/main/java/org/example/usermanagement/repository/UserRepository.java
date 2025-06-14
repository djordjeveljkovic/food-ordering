package org.example.usermanagement.repository;


import org.example.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.permissions WHERE u.email = :email")
    Optional<User> findByEmailWithPermissions(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.permissions")
    List<User> findAllWithPermissions();
}
