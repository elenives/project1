package com.example.learning.repository;

import com.example.learning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByUsernameContaining(String keyword);

    /** 演示原生 SQL 查询 */
    @Query(value = "SELECT * FROM t_user WHERE balance > :minBalance", nativeQuery = true)
    List<User> findByBalanceGreaterThan(@Param("minBalance") java.math.BigDecimal minBalance);
}
