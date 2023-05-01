package com.parkr.parkr.token;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Integer>{
    @Query("""
            SELECT t FROM Token t INNER JOIN User u ON t.user.id  = u.id
            WHERE u.id = :userId AND (t.expired = false OR t.revoked = false)
            """)
    List<Token> findAllValidTokensByUser(Long userId);

    Optional<Token> findByToken(String token);
}
