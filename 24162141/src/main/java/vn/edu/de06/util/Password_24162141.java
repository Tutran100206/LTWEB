package vn.edu.de06.util;

import java.security.*;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Fixed PBKDF2-SHA256 format: P1$ + Base64(16-byte salt || 16-byte derived key), 47 chars. */
public final class Password_24162141 {
    private Password_24162141() {}
    public static String hash(String password) {
        byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt);
        byte[] data = Arrays.copyOf(salt, 32);
        System.arraycopy(derive(password, salt), 0, data, 16, 16);
        return "P1$" + Base64.getEncoder().encodeToString(data);
    }
    public static boolean matches(String password, String stored) {
        if (stored == null || !stored.startsWith("P1$")) return false;
        try {
            byte[] data = Base64.getDecoder().decode(stored.substring(3));
            return data.length == 32 && MessageDigest.isEqual(Arrays.copyOfRange(data,16,32), derive(password,Arrays.copyOf(data,16)));
        } catch (IllegalArgumentException e) { return false; }
    }
    private static byte[] derive(String password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 210000, 128);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
        finally { spec.clearPassword(); }
    }
}
