<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html><html lang="vi"><head><title>Trang chủ</title></head><body><section class="hero"><div><p class="eyebrow">CHÀO MỪNG ĐẾN MARKET 06</p>
<h1>Sản phẩm đa dạng.<br>Cửa hàng gần bạn.</h1><p>Khám phá sản phẩm từ các cửa hàng trong hệ thống.</p>
<a class="button" href="${pageContext.request.contextPath}/products">Khám phá sản phẩm →</a></div><div class="hero-mark" aria-hidden="true">M<span>06</span></div></section>
<c:choose><c:when test="${not empty sessionScope.account.sellerId}"><section class="panel"><p class="eyebrow">TRANG CHỦ SELLER</p><h2><c:out value="${sessionScope.account.sellername}"/></h2><p>Mã cửa hàng: ${sessionScope.account.sellerId}</p><a href="${pageContext.request.contextPath}/seller/home">Xem sản phẩm cửa hàng →</a>
<div class="grid"><c:forEach items="${sellerProducts}" var="p"><article class="panel"><a href="${pageContext.request.contextPath}/product-detail?id=${p.productId}"><c:out value="${p.productName}"/></a><p>Mã: ${p.productCode} · Amount: ${p.amount}</p></article></c:forEach></div></section></c:when>
<c:otherwise><section class="panel"><p class="eyebrow">TRANG CHỦ USER</p><h2><c:choose><c:when test="${empty sessionScope.account}">Tìm sản phẩm bạn cần</c:when><c:otherwise>Xin chào, <c:out value="${sessionScope.account.fullname}"/></c:otherwise></c:choose></h2><p>Xem danh mục, giá và thông tin chi tiết của từng sản phẩm.</p></section></c:otherwise></c:choose></body></html>
