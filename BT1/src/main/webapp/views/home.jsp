<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Trang chính</title>
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<main class="page-shell">
    <div class="card panel">
        <div class="page-top">
            <div>
                <h2 class="brand">Xin chào, <%= request.getSession().getAttribute("user") %></h2>
                <p class="subtitle">Chọn nhanh chức năng bạn muốn xem hôm nay</p>
            </div>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
        </div>

        <div class="feature-grid">
            <a class="feature-item" href="${pageContext.request.contextPath}/horoscope">
                <strong>Tử vi</strong>
                Xem gợi ý theo cung hoàng đạo
            </a>
            <a class="feature-item" href="${pageContext.request.contextPath}/fortune">
                <strong>Bói toán</strong>
                Nhận lời khuyên ngẫu nhiên trong ngày
            </a>
            <a class="feature-item" href="${pageContext.request.contextPath}/lottery">
                <strong>Xổ số</strong>
                Sinh bộ số may mắn cho bạn
            </a>
            <a class="feature-item" href="${pageContext.request.contextPath}/auspicious">
                <strong>Giờ hoàng đạo</strong>
                Xem khung giờ tốt trong ngày
            </a>
        </div>
    </div>
</main>
<script src="${pageContext.request.contextPath}/assets/js/ui.js"></script>
</body>
</html>