<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Bói toán</title>
  <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<main class="page-shell">
  <div class="card panel">
    <h3 class="brand">Bói toán nhanh</h3>
    <p class="subtitle">Nhấn nút để nhận một lời khuyên ngẫu nhiên</p>
    <div style="margin-top:16px;">
      <button id="fortuneBtn" class="btn btn-primary ripple">Bói ngay</button>
    </div>
    <div id="fortuneResult" class="result-box small-muted"></div>
    <div class="inline-actions">
      <a href="${pageContext.request.contextPath}/home" class="btn btn-outline">Quay lại</a>
    </div>
  </div>
</main>
<script src="${pageContext.request.contextPath}/assets/js/ui.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/features.js"></script>
</body>
</html>