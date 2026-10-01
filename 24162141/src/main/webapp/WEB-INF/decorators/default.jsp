<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title><sitemesh:write property="title"/> | Market 06</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"><sitemesh:write property="head"/></head>
<body><header><nav class="container"><a class="brand" href="${pageContext.request.contextPath}/home">M<span>06</span> <small>MARKET</small></a>
<div class="navlinks"><a href="${pageContext.request.contextPath}/home">Trang Chủ</a><a href="${pageContext.request.contextPath}/products">Sản phẩm</a>
<c:if test="${sessionScope.account.roleName eq 'ROLE_USER'}"><a href="${pageContext.request.contextPath}/cart">Giỏ hàng</a><a href="${pageContext.request.contextPath}/orders">Đơn hàng của tôi</a></c:if>
<c:if test="${sessionScope.account.admin}"><a href="${pageContext.request.contextPath}/admin">Trang quản trị</a></c:if>
<c:choose><c:when test="${empty sessionScope.account}"><a class="button" href="${pageContext.request.contextPath}/login">Đăng nhập</a></c:when>
<c:otherwise><span><c:out value="${sessionScope.account.fullname}"/></span><form method="post" action="${pageContext.request.contextPath}/logout"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><button class="outline">Đăng xuất</button></form></c:otherwise></c:choose></div></nav></header>
<main class="container"><c:if test="${not empty message}"><div class="notice" role="status"><c:out value="${message}"/></div></c:if>
<c:if test="${not empty error}"><div class="notice error" role="alert"><c:out value="${error}"/></div></c:if>
<sitemesh:write property="body"/></main>
<footer><div class="container"><strong>Market 06</strong><div>Họ tên: Trần Ngô Anh Tú<br>MSSV: 24162141 <span class="dot">·</span> Mã đề: 06</div></div></footer></body></html>
