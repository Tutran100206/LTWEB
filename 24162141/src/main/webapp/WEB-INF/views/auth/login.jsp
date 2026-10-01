<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head><title>Đăng nhập</title></head><body><section class="auth panel"><p class="eyebrow">TÀI KHOẢN</p><h1>Đăng nhập</h1><p>Chào mừng bạn quay trở lại.</p><form method="post"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><label>Tên đăng nhập<input name="username" type="text" maxlength="50" required value="${fn:escapeXml(param.username)}"></label><label>Mật khẩu<input name="password" type="password" maxlength="128" required autocomplete="new-password"></label><button>Đăng nhập</button></form><p>Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký</a></p></section></body></html>
