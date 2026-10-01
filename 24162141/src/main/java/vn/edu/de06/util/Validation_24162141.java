package vn.edu.de06.util;

public final class Validation_24162141 {
    private Validation_24162141() {}
    public static String text(String value, String label, int max, boolean required) {
        String s = value == null ? "" : value.trim();
        if ((required && s.isEmpty()) || s.length() > max) throw new IllegalArgumentException(label + " phải " + (required ? "có nội dung và " : "") + "không quá " + max + " ký tự.");
        return s;
    }
    public static int id(String s) {
        try { int n = Integer.parseInt(s); if(n > 0) return n; } catch (NumberFormatException ignored) {}
        throw new IllegalArgumentException("Mã không hợp lệ.");
    }
    public static int status(String s) {
        if (!"0".equals(s) && !"1".equals(s)) throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        return Integer.parseInt(s);
    }
    public static String email(String s) {
        s = text(s, "Email", 100, true);
        if(!s.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new IllegalArgumentException("Email không hợp lệ.");
        return s;
    }
    public static String image(String s) {
        s = text(s,"Đường dẫn ảnh",500,false);
        if(!s.isEmpty() && !s.startsWith("https://") && !s.startsWith("http://") && !s.startsWith("assets/")) throw new IllegalArgumentException("Ảnh phải dùng URL http/https hoặc assets/.");
        return s;
    }
}
