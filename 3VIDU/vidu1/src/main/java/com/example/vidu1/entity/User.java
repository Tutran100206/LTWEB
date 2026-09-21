package com.example.vidu1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity @Table(name="vd1_users") @Getter @Setter
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=254) private String email;
    @Column(nullable=false, length=100) private String password;
    @Column(nullable=false, columnDefinition="nvarchar(100)") private String fullName;
    @Column(nullable=false) private boolean enabled;
    @ManyToOne(optional=false, fetch=FetchType.EAGER) @JoinColumn(name="role_id", nullable=false) private Role role;

}
