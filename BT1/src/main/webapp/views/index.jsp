<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home</title>
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>

<body>
<main class="page-shell">
    <div class="card panel text-center">
        <h1 class="brand">Ứng dụng xem vận may</h1>
        <p class="subtitle">Đăng nhập để bắt đầu trải nghiệm các chức năng</p>
        <div class="inline-actions" style="justify-content:center;">
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary ripple">Đăng nhập</a>
        </div>
    </div>
</main>
<script src="${pageContext.request.contextPath}/assets/js/ui.js"></script>
</body>
</html>