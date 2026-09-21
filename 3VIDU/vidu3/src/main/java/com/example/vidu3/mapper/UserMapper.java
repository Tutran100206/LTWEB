package com.example.vidu3.mapper;

import com.example.vidu3.entity.User;
import com.example.vidu3.dto.*;
import org.mapstruct.*;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.ERROR)
public interface UserMapper {
    @Mapping(target="roleName",source="role.name") @Mapping(target="productCount",ignore=true)
    UserDTO toDto(User user);
    @Mapping(target="roleName",source="role.name") @Mapping(target="password",ignore=true)
    UserFormDTO toForm(User user);
    @Mapping(target="id",ignore=true) @Mapping(target="password",ignore=true)
    @Mapping(target="role",ignore=true) @Mapping(target="products",ignore=true)
    void update(UserFormDTO form, @MappingTarget User user);
}
