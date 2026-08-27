<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng ký</title>
</head>
<body>
<h2>Tạo tài khoản</h2>

<c:if test="${alert != null}">
    <p style="color: red;">${alert}</p>
</c:if>

<form action="${pageContext.request.contextPath}/register" method="post">
    <div>
        <label for="username">Tài khoản:</label>
        <input id="username" type="text" name="username" required>
    </div>
    <div>
        <label for="fullname">Họ tên:</label>
        <input id="fullname" type="text" name="fullname" required>
    </div>
    <div>
        <label for="email">Email:</label>
        <input id="email" type="email" name="email" required>
    </div>
    <div>
        <label for="password">Mật khẩu:</label>
        <input id="password" type="password" name="password" required>
    </div>
    <div>
        <label for="phone">Số điện thoại:</label>
        <input id="phone" type="text" name="phone">
    </div>
    <button type="submit">Tạo tài khoản</button>
</form>

<p><a href="${pageContext.request.contextPath}/login">Quay lại Đăng nhập</a></p>
</body>
</html>