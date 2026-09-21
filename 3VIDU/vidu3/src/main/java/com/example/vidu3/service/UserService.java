package com.example.vidu3.service;

import com.example.vidu3.dto.*;
import com.example.vidu3.entity.User;
import com.example.vidu3.mapper.UserMapper;
import com.example.vidu3.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.*;
import java.util.List;
@Service @RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')")
public class UserService {
    private final UserRepository users;
    private final RoleRepository roles;
    private final ProductRepository products;
    private final OtpTokenRepository tokens;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;
    @Transactional(readOnly=true) public Page<UserDTO> search(String keyword,int page,int size) {
        return users.search(keyword,PageRequest.of(Math.max(0,page),Math.clamp(size,1,50),Sort.by("id").descending())).map(u -> {
            UserDTO dto=mapper.toDto(u); dto.setProductCount(products.countByUserId(u.getId())); return dto;
        });
    }
    @Transactional(readOnly=true) public long count() { return users.count(); }
    @Transactional(readOnly=true) public List<UserDTO> owners() { return users.findAll(Sort.by("username")).stream().map(mapper::toDto).toList(); }
    @Transactional(readOnly=true) public UserFormDTO get(Long id) { return mapper.toForm(find(id)); }
    @Transactional public void save(Long id,UserFormDTO form,Long actorId) {
        String email=AuthService.normalize(form.getEmail()), username=AuthService.normalize(form.getUsername());
        boolean duplicate=id==null ? users.existsByEmail(email)||users.existsByUsername(username) : users.existsByEmailAndIdNot(email,id)||users.existsByUsernameAndIdNot(username,id);
        if (duplicate) throw new BusinessException("Email hoặc tên đăng nhập đã được sử dụng.");
        if (id!=null && id.equals(actorId) && (!form.isEnabled() || !"ADMIN".equals(form.getRoleName()))) throw new BusinessException("Không thể tự khóa hoặc hạ quyền tài khoản đang đăng nhập.");
        User user=id==null?new User():find(id);
        if (id!=null && !user.getEmail().equals(email)) tokens.deleteByEmail(user.getEmail());
        mapper.update(form,user); user.setEmail(email); user.setUsername(username);
        user.setRole(roles.findByName(form.getRoleName()).orElseThrow(() -> new BusinessException("Vai trò không hợp lệ.")));
        if (form.getPassword()!=null && !form.getPassword().isBlank()) user.setPassword(encoder.encode(form.getPassword()));
        users.saveAndFlush(user);
    }
    @Transactional public void delete(Long id,Long actorId) {
        if (id.equals(actorId)) throw new BusinessException("Không thể xóa tài khoản đang đăng nhập.");
        User user=find(id);
        if (products.countByUserId(id)>0) throw new BusinessException("Hãy xóa hoặc chuyển quyền sở hữu sản phẩm trước khi xóa người dùng.");
        tokens.deleteByEmail(user.getEmail()); users.delete(user);
    }
    private User find(Long id) { return users.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,"Người dùng không tồn tại.")); }
}
