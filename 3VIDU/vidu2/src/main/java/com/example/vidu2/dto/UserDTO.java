package com.example.vidu2.dto;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String roleName;
    private boolean enabled;
    private String username;
private String images;

}
