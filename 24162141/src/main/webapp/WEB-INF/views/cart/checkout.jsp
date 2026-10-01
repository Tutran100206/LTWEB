<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html><html lang="vi"><head><title>Thanh toán COD</title></head><body><h1>Thanh toán đơn hàng</h1>
<c:choose><c:when test="${empty cart.items}"><div class="panel"><p>Giỏ hàng đang trống.</p><a href="${pageContext.request.contextPath}/products">Chọn sản phẩm</a></div></c:when><c:otherwise>
<div class="grid"><form class="panel" method="post" action="${pageContext.request.contextPath}/checkout"><h2>Thông tin nhận hàng</h2><input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="cartId" value="${cart.cartId}">
<label>Họ tên người nhận<input name="recipientName" maxlength="100" value="${fn:escapeXml(pageContext.request.method eq 'POST' ? param.recipientName : sessionScope.account.fullname)}" required autocomplete="name"></label>
<label>Số điện thoại<input name="phone" type="tel" maxlength="20" pattern="0[0-9]{9,10}" value="${fn:escapeXml(pageContext.request.method eq 'POST' ? param.phone : sessionScope.account.phone)}" required autocomplete="tel"></label>
<label>Địa chỉ nhận hàng<textarea name="address" maxlength="500" rows="4" required autocomplete="street-address"><c:out value="${param.address}"/></textarea></label>
<p><strong>COD — Thanh toán tiền mặt khi nhận hàng.</strong></p><p>Phí vận chuyển: 0 ₫.</p><button>Đặt hàng COD</button></form>
<aside class="panel"><h2>Đơn hàng của bạn</h2><c:forEach items="${cart.items}" var="i"><p><c:out value="${i.productName}"/> × ${i.quantity}<br><strong><fmt:formatNumber value="${i.subtotal}" maxFractionDigits="0"/> ₫</strong></p></c:forEach><hr><p class="price">Tổng thanh toán: <fmt:formatNumber value="${cart.total}" maxFractionDigits="0"/> ₫</p><a href="${pageContext.request.contextPath}/cart">Sửa giỏ hàng</a></aside></div>
</c:otherwise></c:choose></body></html>
