<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Tử vi</title>
  <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<main class="page-shell">
  <div class="card panel">
    <h3 class="brand">Tử vi hôm nay</h3>
    <p class="subtitle">Chọn cung hoàng đạo để nhận thông điệp phù hợp</p>
    <div style="margin-top:16px;">
      <select id="zodiac" class="form-control">
        <option value="">-- Chọn cung --</option>
        <option value="Bạch Dương">Bạch Dương</option>
        <option value="Kim Ngưu">Kim Ngưu</option>
        <option value="Song Tử">Song Tử</option>
        <option value="Cự Giải">Cự Giải</option>
        <option value="Sư Tử">Sư Tử</option>
        <option value="Xử Nữ">Xử Nữ</option>
        <option value="Thiên Bình">Thiên Bình</option>
        <option value="Bọ Cạp">Bọ Cạp</option>
        <option value="Nhân Mã">Nhân Mã</option>
        <option value="Ma Kết">Ma Kết</option>
        <option value="Bảo Bình">Bảo Bình</option>
        <option value="Song Ngư">Song Ngư</option>
      </select>
    </div>
    <div id="horoscopeResult" class="result-box small-muted"></div>
    <div class="inline-actions">
      <a href="${pageContext.request.contextPath}/home" class="btn btn-outline">Quay lại</a>
    </div>
  </div>
</main>
<script src="${pageContext.request.contextPath}/assets/js/ui.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/features.js"></script>
</body>
</html>