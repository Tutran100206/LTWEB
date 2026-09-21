package com.example.vidu3.repository;

import com.example.vidu3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    @org.springframework.data.jpa.repository.Query("select u from User u where lower(u.username) like lower(concat('%', :keyword, '%')) or lower(u.email) like lower(concat('%', :keyword, '%')) or lower(u.fullName) like lower(concat('%', :keyword, '%'))")
    org.springframework.data.domain.Page<User> search(@org.springframework.data.repository.query.Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from User u where u.email = :email")
    Optional<User> lockByEmail(@org.springframework.data.repository.query.Param("email") String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByUsernameAndIdNot(String username, Long id);

}
