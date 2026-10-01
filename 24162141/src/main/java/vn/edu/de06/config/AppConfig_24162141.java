package vn.edu.de06.config;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public final class AppConfig_24162141 {
    private static final Properties VALUES = new Properties();
    static {
        String file = System.getProperty("app.config", System.getenv("APP_CONFIG"));
        if (file != null && !file.isBlank()) {
            try (InputStream in = Files.newInputStream(Paths.get(file))) { VALUES.load(new InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)); }
            catch (IOException e) { throw new ExceptionInInitializerError(e); }
        }
    }
    private AppConfig_24162141() {}
    public static String get(String key, String fallback) {
        String env = System.getenv(key.toUpperCase(java.util.Locale.ROOT).replace('.', '_'));
        return env != null ? env : VALUES.getProperty(key, fallback);
    }
}
