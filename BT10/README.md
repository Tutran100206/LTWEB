# BT10 — Nimbus JWT

Project giữ nguyên Spring Boot **4.1.1**, Java **26**, package `com.example.demo`.
JJWT trong bài giảng đã được thay thế hoàn toàn bằng Nimbus JOSE + JWT.
Không có file `04_JWT.pdf` trong dữ liệu được cung cấp; triển khai dựa trên yêu cầu chi tiết đính kèm, chưa đối chiếu trực tiếp từng trang bài giảng.

## Chạy trên Windows

```powershell
# Đã tạo secret cho workspace hiện tại. Chạy lệnh này khi clone sang máy khác:
powershell -ExecutionPolicy Bypass -File .\scripts\init-jwt-secret.ps1
.\mvnw.cmd clean compile
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Mở http://localhost:8080/login để đăng ký/đăng nhập; hồ sơ ở http://localhost:8080/user/profile.
Giao diện dùng HTML tĩnh và Fetch AJAX, không thêm Thymeleaf. Token lưu trong
`sessionStorage`, gửi qua Bearer header. Logout xóa token tại trình duyệt; token đã phát
vẫn có hiệu lực tới khi hết hạn vì đây là demo stateless, không có danh sách thu hồi.

Project ban đầu chưa có datasource nên bổ sung H2 dạng file `./data/demo` để giữ tài khoản
sau khi khởi động lại. Không bật H2 console. Test dùng H2 in-memory riêng, không sửa dữ liệu ứng dụng.
`spring.jpa.hibernate.ddl-auto=update` phục vụ bài tập.

Secret là 32 byte ngẫu nhiên, mã hóa Base64, lưu ở `.jwt-local.properties` được bỏ qua bởi Git.
`application.properties` import file này; biến môi trường `JWT_SECRET_KEY` có thể ghi đè.
Nếu không có file local thì phải đặt `JWT_SECRET_KEY` hoặc chạy script tạo secret ở trên.
`JWT_EXPIRATION_TIME` mặc định 3600000 (milliseconds), tối thiểu 1000.
Đổi secret làm token cũ mất hiệu lực. Không đưa secret vào Java hay Postman.

## API và Postman

Import `postman/BT10-JWT.postman_collection.json`. Base URL mặc định là
`http://localhost:8080`. Chọn email mới nếu đã đăng ký trước đó.

| Bước | Request | Kết quả |
|---|---|---|
| 1 | POST /auth/signup | 201, thông tin user không có password |
| 2 | POST /auth/login | 200, token và expiresIn; Postman tự lưu token |
| 3 | GET /users/me không có token | 401 JSON |
| 4 | GET /users/me với Bearer token | 200 |
| 5 | GET /users với Bearer token | 200, danh sách không có password |
| 6 | GET /users/me với Bearer abcxyz | 401 JSON |
| 7 | GET /users/me với token hết hạn | 401, JWT token has expired |

Signup: Body → raw → JSON:

```json
{"fullName":"Nguyen Van A","email":"admin@gmail.com","password":"123456"}
```

Login:

```json
{"email":"admin@gmail.com","password":"123456"}
```

Với request được bảo vệ, Authorization → Bearer Token → `{{token}}`.
Email trùng trả 409, sai email/password trả 401, JSON hoặc input sai trả 400.
Handler trả JSON 403 khi không đủ quyền; hiện mọi user đăng nhập đều có thể gọi hai API users.

Test 7 cần thực hiện riêng: dừng app, đặt `$env:JWT_EXPIRATION_TIME='2000'`,
chạy lại app, login, copy token vừa nhận vào biến collection `expiredToken`,
đợi hơn 2 giây rồi gửi request 7. Không sửa payload token vì sẽ làm sai chữ ký.
Sau đó dừng app, `Remove-Item Env:JWT_EXPIRATION_TIME`, chạy lại để dùng thời hạn mặc định.
Khi chạy Collection Runner thông thường, chỉ chọn bước 1–6; bước 7 yêu cầu token hết hạn đã chuẩn bị.

## Thiết kế

- `entity/User`: JPA entity, UserDetails, email unique, timestamps, BCrypt password.
- `dto`: RegisterUserRequest, LoginRequest, LoginResponse, UserResponse; response không chứa password.
- `repository/UserRepository`: tìm user bằng email.
- `service`: UserService, AuthenticationService, JwtService.
- `security`: SecurityConfig, JwtAuthenticationFilter, SecurityErrorHandler.
- `controller`: AuthenticationController, UserController, PageController.
- `exception`: ApiError, ApiExceptionHandler, EmailAlreadyExistsException.
- Static: login.html, profile.html, mainjs.js, style.css.
- Tests: DemoApplicationTests, JwtApiTests.

Dependency `com.nimbusds:nimbus-jose-jwt:10.9.1`.
Không đặt version Spring Security; Spring Boot quản lý.
JWT tạo bằng JWTClaimsSet (sub, iat, exp), SignedJWT, JWSHeader HS256, MACSigner.
Filter parse đúng một lần, bắt buộc thuật toán HS256, verify bằng MACVerifier,
kiểm tra subject/iat/exp/nbf, thời hạn, tải lại user từ database và kiểm tra trạng thái user.
Chỉ sau đó mới đặt Authentication vào SecurityContext. Token sai/hết hạn/user bị xóa trả 401 JSON.
Khóa dưới 256 bit hoặc expiration không hợp lệ làm startup thất bại rõ ràng.

Spring Security dùng SecurityFilterChain, DaoAuthenticationProvider(UserDetailsService),
AuthenticationManager, BCryptPasswordEncoder, constructor injection, Jakarta Servlet,
SessionCreationPolicy.STATELESS. CSRF tắt vì API dùng Bearer token do JavaScript gắn vào header,
không dùng cookie/session để xác thực. Các endpoint ngoài danh sách public đều cần authentication.
Jackson 3 dùng `tools.jackson.databind.ObjectMapper`; annotation JsonIgnore vẫn thuộc
`com.fasterxml.jackson.annotation`.

Maven Wrapper có sửa kiểm tra Target rỗng trong PowerShell để chạy được trên máy hiện tại.
Không đổi Maven distribution, Boot, Java hoặc version Spring Security.

## Xác minh

Đã chạy clean compile thành công và kiểm thử HTTP bằng server thật trên cổng ngẫu nhiên:
signup/login, BCrypt, email trùng, input sai, thiếu token, token sai chữ ký,
sai thuật toán, hết hạn, subject rỗng, iat tương lai, user bị xóa, không tạo session,
không lộ password và static pages.
Đã chạy `spring-boot:run` với H2 file trên cổng 8080, kiểm tra startup và HTTP.
Kiểm thử HTML ở mức HTTP; chưa tự động thao tác giao diện trong trình duyệt.

Tham khảo: [Nimbus trên Maven Central](https://central.sonatype.com/artifact/com.nimbusds/nimbus-jose-jwt/10.9.1),
[Spring Security DaoAuthenticationProvider](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html).
