# Chức năng mua hàng của User

Đăng nhập tài khoản User, chọn **Thêm vào giỏ** ở danh sách hoặc chi tiết sản phẩm. Giỏ hàng lưu trong database theo tài khoản, hỗ trợ cộng thêm sản phẩm, cập nhật số lượng, xóa từng sản phẩm và xóa toàn bộ. Số lượng từ 1 đến `Product.stock`; máy chủ kiểm tra lại, kể cả khi gửi yêu cầu trực tiếp.

Chọn **Thanh toán COD**, nhập tên, số điện thoại và địa chỉ. Đơn mới có trạng thái 1, lưu thông tin nhận hàng, phương thức COD và giá/tên sản phẩm tại thời điểm đặt. Việc trừ tồn kho và tạo đơn nằm trong cùng transaction; nếu thiếu hàng thì rollback. Gửi lại biểu mẫu của đơn đã đặt không tạo đơn trùng. COD là thanh toán khi nhận hàng, chưa thu tiền tại website.

Database mới: chạy `database/de06_24162141.sql`. Database có sẵn: chạy `database/update_cart_orders.sql` (có thể chạy lại, không xóa dữ liệu).

## Quan sát trạng thái từ database

Vào **Đơn hàng của tôi**, sao chép mã đơn, chạy SQL dưới đây trong SSMS trên `DE06_24162141`, rồi tải lại trang hoặc chọn bộ lọc. Chỉ đổi trạng thái của đơn thử nghiệm đã đặt (`buyDate IS NOT NULL`), không đổi giỏ hàng thành đơn.

```sql
USE DE06_24162141;
SELECT cartId, userId, buyDate, status, paymentMethod FROM Cart
WHERE buyDate IS NOT NULL ORDER BY buyDate DESC;

DECLARE @cartId nvarchar(50) = N'DÁN_MÃ_ĐƠN_THỬ_NGHIỆM';
DECLARE @status int = 2; -- thay từ 1 đến 8 theo bảng dưới
UPDATE Cart SET status = @status
WHERE cartId = @cartId AND buyDate IS NOT NULL AND @status BETWEEN 1 AND 8;
```

| Mã | Trạng thái |
|---|---|
| 0 | Giỏ hàng (không hiển thị trong lịch sử) |
| 1 | Đơn hàng mới |
| 2 | Đã xác nhận |
| 3 | Chuẩn bị hàng |
| 4 | Vận chuyển |
| 5 | Giao hàng |
| 6 | Đã giao |
| 7 | Đơn hàng hủy |
| 8 | Đơn hàng hoàn |

Mỗi User chỉ xem được đơn của chính mình. Chỉnh `status` trực tiếp dùng để quan sát giao diện; SQL này không thực hiện nghiệp vụ hoàn kho khi hủy/hoàn đơn.
