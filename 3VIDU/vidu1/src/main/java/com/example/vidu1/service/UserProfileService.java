package com.example.vidu1.service;

import com.example.vidu1.dto.UserDTO;
import com.example.vidu1.mapper.UserMapper;
import com.example.vidu1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository users;
    private final UserMapper mapper;
    @Transactional(readOnly=true)
    public UserDTO find(Long id) { return mapper.toDto(users.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND))); }
}
