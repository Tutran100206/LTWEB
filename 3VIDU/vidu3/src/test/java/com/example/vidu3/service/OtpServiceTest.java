package com.example.vidu3.service;
import com.example.vidu3.entity.*;
import com.example.vidu3.repository.OtpTokenRepository;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class OtpServiceTest {
    OtpTokenRepository tokens; EmailService mail; OtpService service;
    final Instant now=Instant.parse("2026-09-21T00:00:00Z");
    final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(4);
    OtpToken token;
    @BeforeEach void setup() {
        tokens=mock(OtpTokenRepository.class); mail=mock(EmailService.class);
        service=new OtpService(tokens,encoder,mail,Clock.fixed(now,ZoneOffset.UTC));
        token=new OtpToken(); token.setEmail("a@example.com"); token.setType(OtpType.REGISTER); token.setCreatedAt(now.minusSeconds(120)); token.setExpiresAt(now.plusSeconds(300)); token.setCodeHash(encoder.encode("123456"));
        when(tokens.findFirstByEmailAndTypeOrderByIdDesc("a@example.com",OtpType.REGISTER)).thenReturn(Optional.of(token));
    }
    @Test void validCodeIsSingleUse() { service.consume("a@example.com",OtpType.REGISTER,"123456"); assertTrue(token.isUsed()); assertThrows(InvalidOtpException.class,()->service.consume("a@example.com",OtpType.REGISTER,"123456")); }
    @Test void expiredAndWrongPurposeCannotBeUsed() {
        token.setExpiresAt(now); assertThrows(InvalidOtpException.class,()->service.consume("a@example.com",OtpType.REGISTER,"123456"));
        assertThrows(InvalidOtpException.class,()->service.consume("a@example.com",OtpType.RESET_PASSWORD,"123456"));
    }
    @Test void fiveWrongAttemptsInvalidateCode() {
        for(int i=0;i<5;i++) assertThrows(InvalidOtpException.class,()->service.consume("a@example.com",OtpType.REGISTER,"999999"));
        assertTrue(token.isUsed()); assertEquals(5,token.getAttempts()); assertThrows(InvalidOtpException.class,()->service.consume("a@example.com",OtpType.REGISTER,"123456"));
    }
    @Test void resendInvalidatesOldCodeAndStoresHash() {
        when(tokens.findByEmailAndTypeAndUsedFalse("a@example.com",OtpType.REGISTER)).thenReturn(List.of(token));
        service.issue("a@example.com",OtpType.REGISTER);
        assertTrue(token.isUsed());
        var saved=org.mockito.ArgumentCaptor.forClass(OtpToken.class); verify(tokens).save(saved.capture());
        var code=org.mockito.ArgumentCaptor.forClass(String.class); verify(mail).sendOtp(eq("a@example.com"),code.capture(),anyString());
        assertTrue(code.getValue().matches("[0-9]{6}")); assertTrue(encoder.matches(code.getValue(),saved.getValue().getCodeHash())); assertEquals(now.plusSeconds(300),saved.getValue().getExpiresAt());
    }
    @Test void resendHasCooldown() { token.setCreatedAt(now.minusSeconds(30)); assertThrows(BusinessException.class,()->service.issue("a@example.com",OtpType.REGISTER)); verifyNoInteractions(mail); }
}
