package com.bloodconnect.repository;

import com.bloodconnect.entity.User;
import com.bloodconnect.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    List<User> findByRoleOrderByCreatedAtDesc(Role role);

    List<User> findTop10ByOrderByCreatedAtDesc();
}
