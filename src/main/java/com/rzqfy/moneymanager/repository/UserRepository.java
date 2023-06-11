package com.rzqfy.moneymanager.repository;

import com.rzqfy.moneymanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public Optional<User> findFirstByToken(String token);
}
