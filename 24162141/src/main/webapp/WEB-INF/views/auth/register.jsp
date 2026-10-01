<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head><title>Đăng ký</title></head><body><section class="auth panel"><p class="eyebrow">THÀNH VIÊN MỚI</p><h1>Tạo tài khoản</h1><p>Mã kích hoạt sẽ được gửi đến email của bạn.</p><form method="post"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><label>Tên đăng nhập<input name="username" type="text" maxlength="50" required value="${fn:escapeXml(param.username)}"></label><label>Họ tên<input name="fullname" type="text" maxlength="50" required value="${fn:escapeXml(param.fullname)}"></label><label>Email<input name="email" type="email" maxlength="100" required value="${fn:escapeXml(param.email)}"></label><label>Điện thoại<input name="phone" type="tel" maxlength="20" value="${fn:escapeXml(param.phone)}"></label><label>Mật khẩu (6–128 ký tự)<input name="password" type="password" maxlength="128" required autocomplete="new-password"></label><button>Đăng ký &amp; nhận OTP</button></form><p><a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a></p></section></body></html>
