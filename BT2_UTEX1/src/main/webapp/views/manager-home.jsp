<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manager Home</title>
</head>
<body>
<h2>Manager Home</h2>
<p>Xin chào ${sessionScope.account.fullName} (${sessionScope.account.userName})</p>
<p><a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></p>
</body>
</html>
