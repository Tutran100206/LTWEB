package com.example.vidu2.mapper;

import com.example.vidu2.entity.User;
import com.example.vidu2.dto.UserDTO;
import org.mapstruct.*;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.ERROR)
public interface UserMapper {
    @Mapping(target="roleName", source="role.name")

    UserDTO toDto(User user);
}
