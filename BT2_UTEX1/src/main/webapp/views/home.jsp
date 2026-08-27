<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home</title>
</head>
<body>
<h2>Home</h2>

<c:if test="${sessionScope.account != null}">
    <p>Xin chào ${sessionScope.account.fullName}</p>
    <p><a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></p>
</c:if>
</body>
</html>
