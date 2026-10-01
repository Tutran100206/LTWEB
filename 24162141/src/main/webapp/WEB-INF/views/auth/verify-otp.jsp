<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head><title>Xác nhận OTP</title></head><body><section class="auth panel"><p class="eyebrow">KÍCH HOẠT TÀI KHOẢN</p><h1>Xác nhận email</h1><c:choose><c:when test="${not empty sessionScope.pending}"><p>Nhập mã 6 chữ số đã gửi đến <strong><c:out value="${sessionScope.pending.user.email}"/></strong>. Mã có hiệu lực 5 phút, tối đa 5 lần thử.</p><form method="post"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><label>Mã OTP<input name="otp" inputmode="numeric" autocomplete="one-time-code" pattern="[0-9]{6}" maxlength="6" required></label><button>Kích hoạt tài khoản</button></form></c:when><c:otherwise><p>Không có đăng ký đang chờ xác nhận.</p></c:otherwise></c:choose><p><a href="${pageContext.request.contextPath}/register">Đăng ký lại / nhận mã mới</a></p></section></body></html>
