package com.example.vidu3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
@Entity @Table(name="vd3_otp_tokens", indexes=@Index(name="idx_vd3_otp_email_type", columnList="email,type")) @Getter @Setter
public class OtpToken {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=254) private String email;
    @Column(nullable=false, length=100) private String codeHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private OtpType type;
    @Column(nullable=false) private Instant expiresAt;
    @Column(nullable=false) private Instant createdAt;
    private boolean used;
    private int attempts;
}
