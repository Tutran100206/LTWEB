package com.example.vidu3.service;

import com.example.vidu3.dto.*;
import com.example.vidu3.entity.*;
import com.example.vidu3.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    UserRepository users;
    RoleRepository roles;
    OtpService otp;
    AuthService service;
    final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);

    @BeforeEach void setup() {
        users = mock(UserRepository.class);
        roles = mock(RoleRepository.class);
        otp = mock(OtpService.class);
        service = new AuthService(users, roles, encoder, otp);
    }

    @Test void registrationNormalizesIdentityHashesPasswordAndRequiresActivation() {
        var role = new Role(); role.setName("USER");
        when(roles.findByName("USER")).thenReturn(Optional.of(role));
        var form = new RegisterDTO();
        form.setUsername("NewUser"); form.setEmail("NewUser@Example.com");
        form.setFullName("Nguyễn Văn An"); form.setPassword("Secret@123");
        service.register(form);
        var saved = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(users).saveAndFlush(saved.capture());
        assertEquals("newuser", saved.getValue().getUsername());
        assertEquals("newuser@example.com", saved.getValue().getEmail());
        assertFalse(saved.getValue().isEnabled());
        assertEquals("USER", saved.getValue().getRole().getName());
        assertTrue(encoder.matches("Secret@123", saved.getValue().getPassword()));
        verify(otp).issue("newuser@example.com", OtpType.REGISTER);
    }

    @Test void duplicateRegistrationDoesNotSendOtp() {
        var form = new RegisterDTO(); form.setUsername("taken"); form.setEmail("taken@example.com");
        when(users.existsByEmail("taken@example.com")).thenReturn(true);
        assertThrows(BusinessException.class, () -> service.register(form));
        verifyNoInteractions(otp); verify(users, never()).saveAndFlush(any());
    }

    @Test void verificationEnablesAccountOnlyAfterSuccessfulConsume() {
        var user = new User(); user.setEmail("a@example.com");
        when(users.lockByEmail("a@example.com")).thenReturn(Optional.of(user));
        var form = new VerifyOtpDTO(); form.setEmail("a@example.com"); form.setCode("123456");
        doThrow(new InvalidOtpException("Sai mã")).when(otp).consume("a@example.com", OtpType.REGISTER, "123456");
        assertThrows(InvalidOtpException.class, () -> service.verify(form));
        assertFalse(user.isEnabled());
        doNothing().when(otp).consume("a@example.com", OtpType.REGISTER, "123456");
        service.verify(form); assertTrue(user.isEnabled());
    }

    @Test void passwordResetRequiresCorrectPurposeAndSavesBcrypt() {
        var user = new User(); user.setEmail("a@example.com"); user.setEnabled(true); user.setPassword("old-hash");
        when(users.lockByEmail("a@example.com")).thenReturn(Optional.of(user));
        var form = new ResetPasswordDTO(); form.setEmail("a@example.com"); form.setCode("123456"); form.setPassword("NewSecret@123");
        doThrow(new InvalidOtpException("Sai mã")).when(otp).consume("a@example.com", OtpType.RESET_PASSWORD, "123456");
        assertThrows(InvalidOtpException.class, () -> service.reset(form)); assertEquals("old-hash", user.getPassword());
        doNothing().when(otp).consume("a@example.com", OtpType.RESET_PASSWORD, "123456");
        service.reset(form); assertTrue(encoder.matches("NewSecret@123", user.getPassword()));
    }

    @Test void unknownOrDisabledAccountsDoNotReceiveResetCode() {
        service.forgot("missing@example.com");
        var user = new User(); user.setEmail("a@example.com"); user.setEnabled(false);
        when(users.lockByEmail("a@example.com")).thenReturn(Optional.of(user));
        service.forgot("a@example.com"); verifyNoInteractions(otp);
    }
}
