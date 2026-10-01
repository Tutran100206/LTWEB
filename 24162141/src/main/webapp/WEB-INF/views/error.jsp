<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head><title>Không thể thực hiện yêu cầu</title></head><body><section class="panel"><p class="eyebrow">THÔNG BÁO</p><h1>Không thể thực hiện yêu cầu</h1><p>Mã phản hồi: <c:out value="${requestScope['javax.servlet.error.status_code']}" default="503"/></p><c:choose><c:when test="${requestScope['javax.servlet.error.status_code'] == 404}"><p>Không tìm thấy trang hoặc bản ghi.</p></c:when><c:when test="${requestScope['javax.servlet.error.status_code'] == 403}"><p>Bạn không có quyền truy cập hoặc phiên biểu mẫu đã hết hạn.</p></c:when><c:when test="${requestScope['javax.servlet.error.status_code'] == 400}"><p>Mã hoặc dữ liệu yêu cầu không hợp lệ.</p></c:when><c:otherwise><p>Vui lòng kiểm tra yêu cầu hoặc thử lại sau.</p></c:otherwise></c:choose><a class="button" href="${pageContext.request.contextPath}/home">Về trang chủ</a></section></body></html>
