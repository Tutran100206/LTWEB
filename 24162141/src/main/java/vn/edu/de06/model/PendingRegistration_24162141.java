package vn.edu.de06.model;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
public class PendingRegistration_24162141 implements java.io.Serializable {
    private final User_24162141 user;
    private final String otp;
    private final long expiresAt;
    private int attempts;
    public PendingRegistration_24162141(User_24162141 user,String otp,long expiresAt) { this.user=user; this.otp=otp; this.expiresAt=expiresAt; }
    public User_24162141 getUser() { return user; }
    public long getExpiresAt() { return expiresAt; }
    public synchronized void verify(String input,long now) {
        if(now>=expiresAt) throw new IllegalArgumentException("OTP đã hết hạn. Vui lòng đăng ký lại để nhận mã mới.");
        if(attempts>=5) throw new IllegalArgumentException("Đã nhập sai quá 5 lần. Vui lòng đăng ký lại.");
        attempts++;
        if(input==null || !MessageDigest.isEqual(otp.getBytes(StandardCharsets.UTF_8),input.getBytes(StandardCharsets.UTF_8))) throw new IllegalArgumentException("OTP không đúng (tối đa 5 lần thử).");
    }
}
