-- Chạy trên database hiện có, không xóa dữ liệu.
USE DE06_24162141;
GO
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF COL_LENGTH('Cart','recipientName') IS NULL
    ALTER TABLE Cart ADD recipientName nvarchar(100), phone nvarchar(20), address nvarchar(500), paymentMethod varchar(10);
IF COL_LENGTH('CartItem','productName') IS NULL
    ALTER TABLE CartItem ADD productName nvarchar(200);
COMMIT;
GO
-- status: 0=Giỏ hàng, 1=Mới, 2=Đã xác nhận, 3=Chuẩn bị hàng,
-- 4=Vận chuyển, 5=Giao hàng, 6=Đã giao, 7=Hủy, 8=Hoàn.
