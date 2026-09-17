<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>BT4 · Quản lý danh mục và sản phẩm</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<c:url value='/css/admin.css'/>" rel="stylesheet">
</head>
<body data-context-path="${pageContext.request.contextPath}">
<nav class="navbar navbar-expand bg-dark navbar-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="<c:url value='/admin/categories'/>">BT4 · Quản lý</a>
        <div class="navbar-nav flex-row gap-3">
            <a class="nav-link" href="<c:url value='/admin/categories'/>">Danh mục</a>
            <a class="nav-link" href="<c:url value='/admin/products'/>">Sản phẩm</a>
            <a class="nav-link" href="<c:url value='/swagger-ui.html'/>">Swagger</a>
        </div>
    </div>
</nav>
<main class="container pb-5">
    <div id="page-message" class="alert d-none" role="alert"></div>
