package com.example.demo;

import com.example.demo.repository.UserRepository;
import com.example.demo.service.JwtService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:jwt-tests;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "security.jwt.secret-key=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
    "security.jwt.expiration-time=3600000"
})
class JwtApiTests {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final String base;

    @Autowired
    JwtApiTests(ObjectMapper mapper, UserRepository users, PasswordEncoder encoder,
                         JwtService jwt, @Value("${local.server.port}") int port) {
        this.mapper = mapper; this.users = users; this.encoder = encoder; this.jwt = jwt;
        base = "http://localhost:" + port;
    }
    private HttpResponse<String> send(String method, String path, Object body, String token) throws Exception {
        var request = HttpRequest.newBuilder(URI.create(base + path));
        if (token != null) request.header("Authorization", "Bearer " + token);
        if (body != null) request.header("Content-Type", "application/json");
        request.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
            : HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
    private String signup() throws Exception {
        String email = UUID.randomUUID() + "@example.com";
        var response = send("POST", "/auth/signup",
            Map.of("email", email, "password", "123456", "fullName", "Nguyen Van A"), null);
        assertEquals(201, response.statusCode(), response.body());
        assertFalse(response.body().contains("password"));
        var user = users.findByEmail(email).orElseThrow();
        assertTrue(encoder.matches("123456", user.getPassword()));
        assertNotNull(user.getCreatedAt());
        return email;
    }
    private String login(String email) throws Exception {
        var response = send("POST", "/auth/login", Map.of("email", email, "password", "123456"), null);
        assertEquals(200, response.statusCode(), response.body());
        var json = mapper.readTree(response.body());
        assertEquals(3600000, json.get("expiresIn").asLong());
        return json.get("token").asString();
    }
    private String signed(String subject, Instant issued, Instant expires, JWSAlgorithm algorithm, byte[] key) throws Exception {
        var claims = new JWTClaimsSet.Builder().subject(subject).issueTime(Date.from(issued))
            .expirationTime(Date.from(expires)).build();
        var token = new SignedJWT(new JWSHeader(algorithm), claims);
        token.sign(new MACSigner(key));
        return token.serialize();
    }
    private void assertUnauthorized(String token) throws Exception {
        var response = send("GET", "/users/me", null, token);
        assertEquals(401, response.statusCode(), response.body());
        assertEquals(401, mapper.readTree(response.body()).get("status").asInt());
        assertTrue(response.headers().firstValue("content-type").orElse("").contains("application/json"));
    }
    @Test void signupLoginAndProtectedEndpoints() throws Exception {
        String email = signup();
        String token = login(email.toUpperCase(Locale.ROOT));
        assertEquals(email, jwt.extractUsername(token));
        assertFalse(jwt.isTokenExpired(token));
        assertTrue(jwt.isTokenValid(token, users.findByEmail(email).orElseThrow()));
        assertEquals(JWSAlgorithm.HS256, SignedJWT.parse(token).getHeader().getAlgorithm());
        var me = send("GET", "/users/me", null, token);
        assertEquals(200, me.statusCode());
        assertEquals(email, mapper.readTree(me.body()).get("email").asString());
        assertFalse(me.body().contains("password"));
        assertTrue(me.headers().allValues("set-cookie").isEmpty());
        var all = send("GET", "/users", null, token);
        assertEquals(200, all.statusCode());
        assertTrue(mapper.readTree(all.body()).isArray());
        assertFalse(all.body().contains("password"));
        assertUnauthorized(null);
    }
    @Test void rejectsMissingMalformedExpiredAndTamperedTokens() throws Exception {
        String email = signup();
        assertUnauthorized(null);
        assertUnauthorized("abcxyz");
        assertUnauthorized("");
        Instant now = Instant.now();
        String expired = signed(email, now.minusSeconds(120), now.minusSeconds(60), JWSAlgorithm.HS256, new byte[32]);
        assertTrue(jwt.isTokenExpired(expired));
        assertUnauthorized(expired);
        byte[] wrongKey = new byte[32];
        Arrays.fill(wrongKey, (byte) 1);
        assertUnauthorized(signed(email, now.minusSeconds(1), now.plusSeconds(60), JWSAlgorithm.HS256, wrongKey));
        assertUnauthorized(signed(email, now.minusSeconds(1), now.plusSeconds(60), JWSAlgorithm.HS384, new byte[48]));
        assertUnauthorized(signed(" ", now.minusSeconds(1), now.plusSeconds(60), JWSAlgorithm.HS256, new byte[32]));
        assertUnauthorized(signed(email, now.plusSeconds(60), now.plusSeconds(120), JWSAlgorithm.HS256, new byte[32]));
    }
    @Test void rejectsDeletedUser() throws Exception {
        String email = signup();
        String token = login(email);
        users.delete(users.findByEmail(email).orElseThrow());
        assertUnauthorized(token);
    }
    @Test void duplicateEmailBadCredentialsAndValidation() throws Exception {
        String email = signup();
        assertEquals(409, send("POST", "/auth/signup", Map.of("email", email.toUpperCase(Locale.ROOT),
            "password", "123456", "fullName", "Duplicate"), null).statusCode());
        assertEquals(401, send("POST", "/auth/login", Map.of("email", email, "password", "wrong"), null).statusCode());
        assertEquals(401, send("POST", "/auth/login", Map.of("email", "absent@example.com", "password", "wrong"), null).statusCode());
        assertEquals(400, send("POST", "/auth/signup", Map.of("email", "invalid", "password", "123456", "fullName", "A"), null).statusCode());
        assertEquals(400, send("POST", "/auth/signup", Map.of("email", "long@example.com", "password", "ệ".repeat(30), "fullName", "A"), null).statusCode());
    }
    @Test void servesPublicPagesAndAssets() throws Exception {
        for (String path : List.of("/login", "/user/profile", "/login.html", "/profile.html", "/mainjs.js", "/style.css"))
            assertEquals(200, send("GET", path, null, null).statusCode(), path);
    }
}
