# 3VIDU — Spring Boot và Spring Security

Ba ứng dụng Maven độc lập trong cùng workspace. Giữ **Spring Boot 4.1.1**, **Java 26**, Spring Security 7.1.1 do Boot quản lý và MapStruct 1.6.3. Database chính là Microsoft SQL Server; không sử dụng H2.

| Module | Chức năng | Port | Layout |
| --- | --- | --- | --- |
| [vidu1](vidu1/README.md) | Đăng nhập email, USER/ADMIN | 8081 | Thymeleaf fragments, không Layout Dialect |
| [vidu2](vidu2/README.md) | Đăng nhập username hoặc email, avatar | 8082 | Thymeleaf Layout Dialect 4.0.1 |
| [vidu3](vidu3/README.md) | Đăng ký/OTP, reset mật khẩu, CRUD User/Product, Cloudinary | 8083 | Thymeleaf fragments |

## 1. Chuẩn bị

- JDK **26** và `JAVA_HOME` trỏ đúng thư mục JDK 26.
- SQL Server đang chạy, bật TCP/IP; tài khoản SQL có quyền kết nối và tạo/cập nhật bảng trong database.
- Dùng Maven Wrapper có sẵn, không cần cài Maven riêng.

Trong máy hiện tại, Java trên PATH là 26 nhưng `JAVA_HOME` ban đầu trỏ JDK 22. Cập nhật biến môi trường Windows hoặc đặt cho terminal PowerShell hiện tại:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-26.0.2.1'
.\mvnw.cmd -v
```

Nếu JDK cài ở nơi khác, dùng đường dẫn thực tế. Không hạ phiên bản Java hoặc Spring Boot. Wrapper đã được sửa kiểm tra symlink `.m2` để không truy cập phần tử của mảng null trên Windows PowerShell.

## 2. Tạo database

Mở SSMS, kết nối SQL Server và chạy [sql/create-database.sql](sql/create-database.sql). Script chỉ tạo `3VIDU_DB` nếu chưa tồn tại. Hibernate dùng `ddl-auto=update` để tạo/cập nhật bảng khi khởi động.

- Ví dụ 1: `vd1_users`, `vd1_roles`.
- Ví dụ 2: `vd2_users`, `vd2_roles`.
- Ví dụ 3: `vd3_users`, `vd3_roles`, `vd3_otp_tokens`, `vd3_products`.

Họ tên, tên và mô tả sản phẩm dùng `nvarchar` để lưu tiếng Việt. Không xóa hoặc tạo lại database trong quá trình build/test.

## 3. Cấu hình `.env`

Chạy một lần, bỏ qua lệnh tương ứng nếu đã có `.env` của bạn:

```powershell
Copy-Item vidu1/.env.example vidu1/.env
Copy-Item vidu2/.env.example vidu2/.env
Copy-Item vidu3/.env.example vidu3/.env
```

Điền cho **từng module**:

```properties
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=3VIDU_DB;encrypt=false;trustServerCertificate=true;characterEncoding=UTF-8
DB_USERNAME=
DB_PASSWORD=
```

URL trên là ví dụ cho SQL Server local; sửa host, port hoặc database theo máy bạn. Không có mật khẩu thật trong source.

`.env` được đọc theo định dạng Java properties bằng `spring.config.import=optional:file:.env[.properties]`: không thêm `export`, không bọc giá trị bằng dấu nháy. Nếu giá trị có dấu `\`, ghi `\\`. Biến môi trường của hệ điều hành có thể ghi đè cấu hình trong file.

Maven plugin đặt working directory là thư mục module, nên lệnh `-pl viduN spring-boot:run` ở root đọc đúng `viduN/.env`. Khi chạy main class từ VS Code, đặt working directory là module tương ứng. Khi chạy JAR, đứng trong thư mục module chứa `.env`.

Ví dụ 3 cần thêm:

| Biến | Nội dung |
| --- | --- |
| `MAIL_HOST` | Máy chủ SMTP, mặc định `smtp.gmail.com` |
| `MAIL_PORT` | Cổng SMTP, mặc định `587` |
| `MAIL_USERNAME` | Địa chỉ email gửi OTP |
| `MAIL_PASSWORD` | Mật khẩu SMTP; với Gmail dùng App Password |
| `CLOUDINARY_CLOUD_NAME` | Cloud name của tài khoản Cloudinary |
| `CLOUDINARY_API_KEY` | API key |
| `CLOUDINARY_API_SECRET` | API secret |

Chưa có SMTP thì chức năng gửi OTP không hoạt động; giao diện báo lỗi gửi mail hoặc thông báo chung ở luồng quên mật khẩu. Chưa có Cloudinary vẫn có thể tạo sản phẩm không chọn ảnh. Các file `.env` được Git ignore.

## 4. Build và kiểm thử

```powershell
.\mvnw.cmd clean test
```

Build độc lập từng module:

```powershell
.\mvnw.cmd -f vidu1/pom.xml clean test
.\mvnw.cmd -f vidu2/pom.xml clean test
.\mvnw.cmd -f vidu3/pom.xml clean test
```

MapStruct sinh mã tại `viduN/target/generated-sources/annotations`. Lombok và `lombok-mapstruct-binding` được khai báo trong annotation processor paths của parent POM.

Kiểm thử không cần DB/SMTP/Cloudinary thật: kiểm tra MVC + Security + HTML Thymeleaf bằng MockMvc, xác thực BCrypt, phân quyền, CSRF, OTP, quyền sở hữu và vòng đời ảnh. Kiểm thử Spring Boot context dùng mapping/dialect SQL Server và kiểm tra khởi tạo repository/query; tắt truy cập JDBC metadata và schema generation **chỉ trong test**. Không thay SQL Server bằng database khác.

Các kiểm thử này không thay thế kiểm thử ghi/đọc SQL Server thật, gửi email thật và upload/xóa ảnh thật. Sau khi điền credentials, làm theo checklist trong README mỗi module.

## 5. Chạy ứng dụng

Mở ba terminal tại root nếu muốn chạy đồng thời:

```powershell
.\mvnw.cmd -pl vidu1 spring-boot:run
.\mvnw.cmd -pl vidu2 spring-boot:run
.\mvnw.cmd -pl vidu3 spring-boot:run
```

- Ví dụ 1: http://localhost:8081
- Ví dụ 2: http://localhost:8082
- Ví dụ 3: http://localhost:8083

Session cookie lần lượt là `VIDU1SESSION`, `VIDU2SESSION`, `VIDU3SESSION` để ba ứng dụng trên `localhost` không ghi đè cookie của nhau. Đây vẫn là session authentication do Spring Security quản lý, không dùng JWT.

## 6. Tài khoản học tập

Các tài khoản được tạo sau khi ứng dụng kết nối SQL Server thành công lần đầu. Mật khẩu chung: **`Demo@12345`**, chỉ lưu BCrypt trong DB.

| Module | ADMIN | USER | Username (ví dụ 2 và 3) |
| --- | --- | --- | --- |
| vidu1 | `admin@vidu1.local` | `user@vidu1.local` | Đăng nhập bằng email |
| vidu2 | `admin@vidu2.local` | `user@vidu2.local` | `admin` / `user` |
| vidu3 | `admin@vidu3.local` | `user@vidu3.local` | `admin` / `user` |

Seed chạy mặc định, kiểm tra tồn tại trước khi tạo và không đặt lại mật khẩu khi restart. `SEED_DEMO=false` tắt initializer (chỉ dùng sau khi đã có vai trò/tài khoản cần thiết). Địa chỉ `.local` chỉ phục vụ đăng nhập mẫu; đăng ký/reset qua email cần địa chỉ nhận thư thật.

## 7. Cấu trúc mã

Mỗi module có `entity`, `repository`, `dto`, `mapper`, `service`, `security`, `controller`, `config`, `templates` và `static`. Controller nhận DTO và validation; service xử lý nghiệp vụ/transaction; repository làm việc với JPA. Controller không bind entity trực tiếp.

Tài liệu tham khảo: [Spring Security 7 — DaoAuthenticationProvider](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/passwords/dao-authentication-provider.html), [Thymeleaf layouts](https://www.thymeleaf.org/doc/articles/layouts.html), [Cloudinary Java SDK](https://cloudinary.com/documentation/java_integration).
