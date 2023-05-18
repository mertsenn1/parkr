package com.parkr.parkr.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.parkr.parkr.car.Car;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>
{

    User findByMailAndPassword(String mail, String password);
    Optional<User> findByMail(String mail);

    @Query("SELECT c FROM Car c JOIN c.user u ON u.id = ?1")
    List<Car> getCarsOfUser(Long id);

    Optional<User> findById(Long id);
    
}
