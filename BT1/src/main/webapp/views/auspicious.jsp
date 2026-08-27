<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Giờ hoàng đạo</title>
  <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<main class="page-shell">
  <div class="card panel">
    <h3 class="brand">Giờ hoàng đạo hôm nay</h3>
    <p class="subtitle">Danh sách khung giờ tốt để tham khảo</p>
    <div id="auspiciousResult" class="result-box hours-list"></div>
    <div class="inline-actions">
      <a href="${pageContext.request.contextPath}/home" class="btn btn-outline">Quay lại</a>
    </div>
  </div>
</main>
<script src="${pageContext.request.contextPath}/assets/js/ui.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/features.js"></script>
</body>
</html>