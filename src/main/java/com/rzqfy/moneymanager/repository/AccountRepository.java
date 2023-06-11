package com.rzqfy.moneymanager.repository;

import com.rzqfy.moneymanager.entity.Account;
import com.rzqfy.moneymanager.entity.Group;
import com.rzqfy.moneymanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("SELECT a FROM Account a JOIN a.group g WHERE g.user = :user AND a.deletedAt IS NULL")
    List<Account> findAllByUserAndDeletedAtIsNull(@Param("user") User user);

    @Query("SELECT a FROM Account a JOIN a.group g WHERE g.user = :user AND a.deletedAt IS NOT NULL")
    List<Account> findAllByUserAndDeletedAtIsNotNull(@Param("user") User user);

    @Query("SELECT a FROM Account a JOIN a.group g WHERE g.user = :user AND a.id = :accountId")
    Optional<Account> findFirstByUserAndAccountId(@Param("user") User user, @Param("accountId") String accountId);

    @Query("SELECT a FROM Account a JOIN a.group g WHERE g.user = :user AND a.id = :accountId AND a.deletedAt IS NULL")
    Optional<Account> findFirstByUserAndAccountIdAndDeletedAtIsNull(@Param("user") User user, @Param("accountId") String accountId);

    @Query("SELECT a FROM Account a JOIN a.group g WHERE g.user = :user AND a.id = :accountId AND a.deletedAt IS NOT NULL")
    Optional<Account> findFirstByUserAndAccountIdAndDeletedAtIsNotNull(@Param("user") User user, @Param("accountId") String accountId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Account a JOIN a.group g WHERE g.user = :user AND a.id != :accountId AND a.name= :newName")
    boolean existsByUserAndAccountId(@Param("user") User user, @Param("accountId") String accountId, @Param("newName") String newName);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Account a JOIN a.group g WHERE g.user = :user AND a.name= :newName")
    boolean existsByUser(@Param("user") User user, @Param("newName") String newName);

    @Query("SELECT a FROM Account a JOIN a.group g WHERE g.user = :user AND g.id = :groupId AND a.deletedAt IS NULL")
    List<Account> findAllByUserAndGroupIdAndDeletedAtIsNull(@Param("user") User user, @Param("groupId") String groupId);
}
