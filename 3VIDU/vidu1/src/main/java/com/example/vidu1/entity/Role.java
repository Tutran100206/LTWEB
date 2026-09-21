package com.example.vidu1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity @Table(name="vd1_roles") @Getter @Setter
public class Role {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=20) private String name;
}
