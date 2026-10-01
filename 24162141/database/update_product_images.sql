USE DE06_24162141;
GO
SET XACT_ABORT ON;
BEGIN TRANSACTION;
UPDATE Product SET images=N'assets/products/laptop-office.jpg' WHERE productCode=100001;
UPDATE Product SET images=N'assets/products/laptop-pro.jpg' WHERE productCode=100002;
UPDATE Product SET images=N'assets/products/phone-nova.jpg' WHERE productCode=100003;
UPDATE Product SET images=N'assets/products/earbuds.jpg' WHERE productCode=100004;
UPDATE Product SET images=N'assets/products/keyboard.jpg' WHERE productCode=100005;
UPDATE Product SET images=N'assets/products/mouse.jpg' WHERE productCode=100006;
UPDATE Product SET images=N'assets/products/laptop-slim.jpg' WHERE productCode=100007;
UPDATE Product SET images=N'assets/products/phone-mini.jpg' WHERE productCode=100008;
UPDATE Product SET images=N'assets/products/speaker.jpg' WHERE productCode=100009;
UPDATE Product SET images=N'assets/products/charger.jpg' WHERE productCode=100010;
UPDATE Product SET images=N'assets/products/headphones.jpg' WHERE productCode=100011;
UPDATE Product SET images=N'assets/products/laptop-stand.jpg' WHERE productCode=100012;
COMMIT TRANSACTION;
GO
