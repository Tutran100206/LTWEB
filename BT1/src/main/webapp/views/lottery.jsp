<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Xổ số</title>
  <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<main class="page-shell">
  <div class="card panel">
    <h3 class="brand">Xổ số may mắn</h3>
    <p class="subtitle">Tạo ngẫu nhiên 6 số từ 1 đến 45</p>
    <div style="margin-top:16px;">
      <button id="lotteryBtn" class="btn btn-primary ripple">Tạo dãy số</button>
    </div>
    <div id="lotteryResult" class="result-box result-strong"></div>
    <div class="inline-actions">
      <a href="${pageContext.request.contextPath}/home" class="btn btn-outline">Quay lại</a>
    </div>
  </div>
</main>
<script src="${pageContext.request.contextPath}/assets/js/ui.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/features.js"></script>
</body>
</html>