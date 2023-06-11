package com.rzqfy.moneymanager.repository;

import com.rzqfy.moneymanager.entity.Transaction;
import com.rzqfy.moneymanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query("SELECT t FROM Transaction t " +
            "JOIN t.account a " +
            "JOIN a.group g " +
            "WHERE g.user = :user " +
            "AND t.id = :transactionId")
    public Optional<Transaction> findByIdAndUser(@Param("user") User user, @Param("transactionId") String transactionId);

    @Query("SELECT t FROM Transaction t " +
            "JOIN t.account a " +
            "JOIN a.group g " +
            "WHERE g.user = :user " +
            "AND a.id=(" +
            "CASE " +
            "WHEN :accountQueryParam IS NOT NULL THEN :accountQueryParam " +
            "ELSE a.id " +
            "END) " +
            "ORDER BY t.date DESC")
    public List<Transaction> findAllByUserAndFilter(@Param("user") User user, @Param("accountQueryParam") String accountQueryParam);
}
