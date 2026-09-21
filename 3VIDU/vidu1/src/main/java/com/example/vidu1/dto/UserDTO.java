package com.example.vidu1.dto;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String roleName;
    private boolean enabled;

}
