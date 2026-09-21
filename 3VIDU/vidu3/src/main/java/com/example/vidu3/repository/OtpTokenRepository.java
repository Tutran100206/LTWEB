package com.example.vidu3.repository;

import com.example.vidu3.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface OtpTokenRepository extends JpaRepository<OtpToken,Long> {
    Optional<OtpToken> findFirstByEmailAndTypeOrderByIdDesc(String email, OtpType type);
    List<OtpToken> findByEmailAndTypeAndUsedFalse(String email, OtpType type);
    void deleteByEmail(String email);
}
