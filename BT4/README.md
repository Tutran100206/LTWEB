# BT4 — CRUD Category / Product bằng JSP và jQuery AJAX

Project gốc được bổ sung trực tiếp, giữ Spring Boot 4.1.1 và Java 26.
Database SQL Server: localhost:1433, tên BT4. Thông tin kết nối mặc định nằm
trong application.properties; có thể ghi đè bằng DB_URL, DB_USERNAME, DB_PASSWORD.

## Chạy trên Windows

Database BT4 đã được tạo trên máy hiện tại. Khi chuyển máy, chạy
scripts/create-database.sql trong SQL Server Management Studio trước.
Hibernate tạo/cập nhật hai bảng Category và Product, không drop bảng.

PowerShell:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-26.0.2.1'
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

JAVA_HOME của máy ban đầu đang trỏ JDK 22; cần đặt JDK 26 như trên.
Nếu vị trí JDK trên máy khác khác, thay đường dẫn tương ứng.

Hoặc chạy bản WAR sau khi build:

```powershell
& "$env:JAVA_HOME\bin\java.exe" -jar target/demo-0.0.1-SNAPSHOT.war
```

## Trang và API

- Danh mục: http://localhost:8080/admin/categories
- Sản phẩm: http://localhost:8080/admin/products
- Swagger: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

| Phương thức | Category | Product |
| --- | --- | --- |
| GET danh sách | /api/category | /api/product |
| GET chi tiết | /api/category/{id} | /api/product/{id} |
| POST multipart | /api/category/addCategory | /api/product/addProduct |
| PUT multipart | /api/category/updateCategory | /api/product/updateProduct |
| DELETE | /api/category/deleteCategory?categoryId=ID | /api/product/deleteProduct?productId=ID |

Response: `{ "status": true/false, "message": "...", "body": ... }`.
Lỗi validation: 400; không tồn tại: 404; trùng danh mục/đang được tham chiếu: 409;
upload quá lớn: 413. Thêm thành công: 201.

- Category: categoryName, icon (ảnh tùy chọn); update thêm categoryId.
- Product: productName, quantity, unitPrice, discount, description, categoryId,
  status (true/false), imageFile (tùy chọn); update thêm productId.
- Discount là phần trăm 0–100; unitPrice không âm, tối đa 2 chữ số thập phân;
  quantity là số nguyên không âm. Tên danh mục không trùng, không phân biệt hoa/thường.
- Ảnh PNG/JPEG/GIF, tối đa 5 MB và 25 triệu pixel. Lưu bằng UUID trong uploads/,
  đọc qua /uploads/{filename}. Có thể thay thư mục bằng UPLOAD_DIR.
- Không chọn ảnh khi sửa sẽ giữ ảnh cũ; createDate do server tạo và giữ nguyên.
  Thay/xóa ảnh cũ chỉ thực hiện sau khi transaction database thành công.
- Hai trang lấy toàn bộ dữ liệu và danh mục chọn qua AJAX, dùng chung layout JSP.
  Bootstrap và jQuery tải từ CDN, cần kết nối Internet khi mở trang.
- Project gốc chưa có Security, SiteMesh hoặc template; không có cấu hình bảo mật
  cũ bị vô hiệu hóa. Không thêm dữ liệu mẫu tự động.

## Kiểm tra

```powershell
python scripts/smoke_test.py
```

Ứng dụng phải đang chạy. Script tạo dữ liệu kiểm thử duy nhất rồi xóa chính
dữ liệu đó, kiểm tra CRUD, multipart PUT, validation, quan hệ, ảnh, JSP và Swagger.
Đặt TEST_BASE_URL nếu đổi port/context path.

Kiểm tra giao diện bằng Chrome (cần Node.js và Chrome tại đường dẫn trong script):

```powershell
npm.cmd install --prefix target/browser-test --no-save --package-lock=false playwright
node scripts/browser_test.cjs
```

Đã xác nhận: Maven clean compile và verify BUILD SUCCESS; 46 kiểm tra HTTP trên
ứng dụng chạy trực tiếp và 46 kiểm tra trên WAR có context path /bt4; Chrome
hoàn thành thêm/sửa/xóa hai bảng, upload, giữ ảnh/ngày tạo và xử lý lỗi tham chiếu,
không ghi nhận lỗi JavaScript.

## Các file bổ sung

- entity/: Category, Product.
- repository/: CategoryRepository, ProductRepository.
- service/: CategoryService, ProductService, StorageService.
- dto/: ApiResponse, CategoryForm, ProductForm, CategoryDto, ProductDto.
- controller/: CategoryApiController, ProductApiController, ApiExceptionHandler,
  PageController, ImageController.
- src/main/webapp/WEB-INF/views/: categories.jsp, products.jsp,
  layout/header.jsp, layout/footer.jsp.
- src/main/resources/static/: js/crud.js, css/admin.css.
- scripts/create-database.sql, scripts/smoke_test.py, scripts/browser_test.cjs và README.md.

Đã sửa: pom.xml, src/main/resources/application.properties, .gitignore.
Đóng gói WAR để hỗ trợ JSP; Springdoc 3.1.1 theo tài liệu hỗ trợ Spring Boot 4:
https://springdoc.org/ và https://docs.spring.io/spring-boot/how-to/deployment/traditional-deployment.html
