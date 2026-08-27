<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Lỗi đăng nhập</title>
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>

<body>
<main class="page-shell">
    <div class="card panel text-center">
        <h2 class="brand">Đăng nhập thất bại</h2>
        <p class="subtitle">Tên đăng nhập hoặc mật khẩu không đúng.</p>
        <div class="inline-actions" style="justify-content:center;">
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary ripple">Đăng nhập lại</a>
        </div>
    </div>
</main>

<script src="${pageContext.request.contextPath}/assets/js/ui.js"></script>
</body>
</html>