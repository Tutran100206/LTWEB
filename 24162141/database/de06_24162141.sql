-- Đề 06 / MSSV 24162141. Chạy bằng SSMS hoặc sqlcmd, SQL Server 2012+.
-- Chỉ khởi tạo database mới; không xóa/ghi đè database có sẵn.
USE master;
GO
IF DB_ID(N'DE06_24162141') IS NULL
    EXEC(N'CREATE DATABASE DE06_24162141');
GO
USE DE06_24162141;
GO
SET XACT_ABORT ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
SET ANSI_PADDING ON;
SET ANSI_WARNINGS ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET ARITHABORT ON;
SET NUMERIC_ROUNDABORT OFF;
IF EXISTS(SELECT 1 FROM sys.tables WHERE is_ms_shipped=0)
    THROW 50001, N'Database đã có bảng. Script không ghi đè dữ liệu.', 1;
BEGIN TRANSACTION;
CREATE TABLE UserRoles (
    roleId int IDENTITY(1,1) PRIMARY KEY,
    roleName nvarchar(50)
);
CREATE TABLE Seller (
    sellerId int IDENTITY(1,1) PRIMARY KEY,
    sellername nvarchar(50), images nvarchar(500), status int
);
CREATE TABLE Category (
    categoryId int IDENTITY(1,1) PRIMARY KEY,
    categoryName nvarchar(200), images nvarchar(500), status int
);
CREATE TABLE Users (
    userId int IDENTITY(1,1) PRIMARY KEY,
    username nvarchar(50), email nvarchar(100), fullname nvarchar(50),
    password nvarchar(50), images nvarchar(500), phone nvarchar(20), status int,
    code nvarchar(50), roleId int, sellerId int,
    CONSTRAINT FK_Users_Roles FOREIGN KEY(roleId) REFERENCES UserRoles(roleId),
    CONSTRAINT FK_Users_Seller FOREIGN KEY(sellerId) REFERENCES Seller(sellerId)
);
CREATE UNIQUE INDEX UX_Users_username ON Users(username) WHERE username IS NOT NULL;
CREATE UNIQUE INDEX UX_Users_email ON Users(email) WHERE email IS NOT NULL;
CREATE TABLE Product (
    productId int IDENTITY(1,1) PRIMARY KEY,
    productName nvarchar(200), productCode bigint, categoryId int,
    description nvarchar(500), price float, amount int, stock int,
    images nvarchar(500), wishlist int, status int, createDate date, sellerId int,
    CONSTRAINT FK_Product_Category FOREIGN KEY(categoryId) REFERENCES Category(categoryId),
    CONSTRAINT FK_Product_Seller FOREIGN KEY(sellerId) REFERENCES Seller(sellerId)
);
-- Giữ nvarchar(50) theo hình đề. NEWID() tự sinh ID chuỗi; IDENTITY chỉ dùng cho ID số.
CREATE TABLE Cart (
    cartId nvarchar(50) NOT NULL DEFAULT CONVERT(nvarchar(50),NEWID()) PRIMARY KEY,
    userId int, buyDate datetime, status int,
    recipientName nvarchar(100), phone nvarchar(20), address nvarchar(500), paymentMethod varchar(10),
    CONSTRAINT FK_Cart_Users FOREIGN KEY(userId) REFERENCES Users(userId)
);
CREATE TABLE CartItem (
    cartItemId nvarchar(50) NOT NULL DEFAULT CONVERT(nvarchar(50),NEWID()) PRIMARY KEY,
    quantity int, unitPrice float, productId int, cartId nvarchar(50), productName nvarchar(200),
    CONSTRAINT FK_CartItem_Product FOREIGN KEY(productId) REFERENCES Product(productId),
    CONSTRAINT FK_CartItem_Cart FOREIGN KEY(cartId) REFERENCES Cart(cartId)
);
INSERT INTO UserRoles(roleName) VALUES(N'ROLE_USER'),(N'ROLE_ADMIN');
INSERT INTO Seller(sellername,images,status) VALUES
(N'Cửa hàng An Phát',N'assets/product.svg',1),
(N'Tech Việt',N'assets/product.svg',1),
(N'Góc Phụ Kiện',N'assets/product.svg',1);
INSERT INTO Category(categoryName,images,status) VALUES
(N'Laptop',N'assets/product.svg',1),(N'Điện thoại',N'assets/product.svg',1),
(N'Âm thanh',N'assets/product.svg',1),(N'Phụ kiện',N'assets/product.svg',1),
(N'Màn hình',N'assets/product.svg',1),(N'Danh mục thử xóa',N'assets/product.svg',0);
-- Seed passwords are PBKDF2-SHA256, 210000 rounds, 16-byte salt + 16-byte key.
-- The following INSERT is generated once and committed with the project.
INSERT INTO Users(username,email,fullname,password,images,phone,status,code,roleId,sellerId) VALUES
(N'admin',N'admin@example.com',N'Quản trị viên',N'P1$hzCpgXIOsdi7db5uYk0Yg8e7RFfejtU4jU4owUlLKJE=',N'assets/product.svg',N'0900000000',1,NULL,2,NULL),
(N'user1',N'user1@example.com',N'Nguyễn An',N'P1$yvL5N+iC7SKmh8SNNlBJkdwoGzppis+SSkWGeefjhRM=',N'assets/product.svg',N'0900000001',1,NULL,1,NULL),
(N'seller1',N'seller1@example.com',N'Trần Bình',N'P1$R1Pjqjt7AVQR/A52n7A6/VV9pWbwUr6amVrngUU/h6g=',N'assets/product.svg',N'0900000002',1,NULL,1,1),
(N'seller2',N'seller2@example.com',N'Lê Chi',N'P1$rK2KS5eM5GiOuXcmVL9jvoL/kXAEQz1iU0aFso1Cxmk=',N'assets/product.svg',N'0900000003',1,NULL,1,2),
(N'user2',N'user2@example.com',N'Phạm Dung',N'P1$5ocmUr2TY1MNarYRfNjfVN1LHS2zhhsTKk/g03sHrco=',N'assets/product.svg',N'0900000004',1,NULL,1,NULL),
(N'user3',N'user3@example.com',N'Hoàng Hà',N'P1$thU8aWOrtmRZptiGXIfhlXBTsoTbLuP4SmOPp6S8XLY=',N'assets/product.svg',N'0900000005',1,NULL,1,NULL),
(N'locked',N'locked@example.com',N'Tài khoản khóa',N'P1$ejMlGXI034EjE8+j3GxPDsvC/jbmdXviFUMPqXaWrCg=',N'assets/product.svg',N'0900000006',0,NULL,1,NULL);
INSERT INTO Product(productName,productCode,categoryId,description,price,amount,stock,images,wishlist,status,createDate,sellerId) VALUES
(N'Laptop Văn Phòng A14',100001,1,N'Laptop Văn Phòng A14 – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',12500000,10,30,N'assets/products/laptop-office.jpg',0,1,'2026-09-24',1),
(N'Laptop Đồ Họa Pro',100002,1,N'Laptop Đồ Họa Pro – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',24900000,11,31,N'assets/products/laptop-pro.jpg',0,1,'2026-09-24',1),
(N'Điện thoại Nova',100003,2,N'Điện thoại Nova – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',6900000,12,32,N'assets/products/phone-nova.jpg',0,1,'2026-09-24',1),
(N'Tai nghe Air Sound',100004,3,N'Tai nghe Air Sound – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',890000,13,33,N'assets/products/earbuds.jpg',0,1,'2026-09-24',1),
(N'Bàn phím Cơ K68',100005,4,N'Bàn phím Cơ K68 – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',1250000,14,34,N'assets/products/keyboard.jpg',0,1,'2026-09-24',2),
(N'Chuột Không Dây M2',100006,4,N'Chuột Không Dây M2 – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',350000,15,35,N'assets/products/mouse.jpg',0,1,'2026-09-24',2),
(N'Laptop Slim 15',100007,1,N'Laptop Slim 15 – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',16900000,16,36,N'assets/products/laptop-slim.jpg',0,1,'2026-09-24',2),
(N'Điện thoại Mini 5G',100008,2,N'Điện thoại Mini 5G – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',8990000,17,37,N'assets/products/phone-mini.jpg',0,1,'2026-09-24',2),
(N'Loa Bluetooth S3',100009,3,N'Loa Bluetooth S3 – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',1450000,18,38,N'assets/products/speaker.jpg',0,1,'2026-09-24',3),
(N'Sạc USB-C 65W',100010,4,N'Sạc USB-C 65W – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',450000,19,39,N'assets/products/charger.jpg',0,1,'2026-09-24',3),
(N'Tai nghe Studio',100011,3,N'Tai nghe Studio – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',2190000,20,40,N'assets/products/headphones.jpg',0,1,'2026-09-24',3),
(N'Giá đỡ Laptop',100012,4,N'Giá đỡ Laptop – thiết kế tiện dụng, phù hợp học tập và làm việc. Bảo hành 12 tháng. Dữ liệu mẫu phục vụ bài thi đề 06.',290000,21,41,N'assets/products/laptop-stand.jpg',0,1,'2026-09-24',3);
COMMIT TRANSACTION;
GO
