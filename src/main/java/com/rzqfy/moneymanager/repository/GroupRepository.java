package com.rzqfy.moneymanager.repository;

import com.rzqfy.moneymanager.entity.Group;
import com.rzqfy.moneymanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {
    public boolean existsByNameAndUser(String name, User user);
    public boolean existsByNameAndUserAndIdNot(String name, User user, String id);

    public Optional<Group> findFirstByIdAndUserAndDeletedAtIsNotNull(String id, User user);
    public Optional<Group> findFirstByIdAndUserAndDeletedAtIsNull(String id, User user);
    public List<Group> findAllByUserAndDeletedAtIsNull(User user);
    public List<Group> findAllByUserAndDeletedAtIsNotNull(User user);

    public Optional<Group> findFirstByIdAndUser(String id, User user);
}
