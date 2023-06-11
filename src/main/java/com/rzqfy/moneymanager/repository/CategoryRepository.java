package com.rzqfy.moneymanager.repository;

import com.rzqfy.moneymanager.entity.Category;
import com.rzqfy.moneymanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    public Optional<Category> findFirstByIdAndUser(String id, User user);

    public boolean existsByUserAndTypeAndNameAndIdNot(User user, String type, String name, String id);

    public boolean existsByUserAndTypeAndName(User user, String type, String name);

    public Optional<Category> findFirstByIdAndUserAndDeletedAtIsNull(String id, User user);

    public Optional<Category> findFirstByIdAndUserAndDeletedAtIsNotNull(String id, User user);

    public List<Category> findAllByUserAndDeletedAtIsNull(User user);

    public List<Category> findAllByUserAndDeletedAtIsNotNull(User user);
    public Optional<Category> findFirstByIdAndUserAndTypeAndDeletedAtIsNull(String id, User user, String type);
}
