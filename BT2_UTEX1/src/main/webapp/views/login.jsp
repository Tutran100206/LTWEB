<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập</title>
</head>
<body>
<h2>Đăng nhập</h2>

<c:if test="${alert != null}">
    <p style="color: red;">${alert}</p>
</c:if>

<form action="${pageContext.request.contextPath}/login" method="post">
    <div>
        <label for="username">Tài khoản:</label>
        <input id="username" type="text" name="username" required>
    </div>
    <div>
        <label for="password">Mật khẩu:</label>
        <input id="password" type="password" name="password" required>
    </div>
    <div>
        <label>
            <input type="checkbox" name="remember" value="on"> Ghi nhớ đăng nhập
        </label>
    </div>
    <button type="submit">Đăng nhập</button>
</form>

<p><a href="${pageContext.request.contextPath}/register">Đăng ký</a></p>
</body>
</html>