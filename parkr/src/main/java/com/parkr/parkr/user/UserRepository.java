package com.parkr.parkr.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>
{
    List<User> findAllByIsOwnerIsTrue();

    List<User> findAllByIsOwnerIsFalse();

    User findByMailAndPassword(String mail, String password);
}
