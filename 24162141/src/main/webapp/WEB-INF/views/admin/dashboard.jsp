<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html><html lang="vi"><head><title>Trang quản trị</title></head><body><p class="eyebrow">ADMIN</p><h1>Trang quản trị</h1><p>Quản lý dữ liệu người dùng và danh mục sản phẩm.</p><div class="grid"><a class="panel dashboard-card" href="${pageContext.request.contextPath}/admin/users"><span>01</span><h2>Người dùng →</h2><p>Thêm, xem, sửa, xóa và phân trang.</p></a><a class="panel dashboard-card" href="${pageContext.request.contextPath}/admin/categories"><span>02</span><h2>Danh mục →</h2><p>Quản lý tên, ảnh và trạng thái danh mục.</p></a></div></body></html>
