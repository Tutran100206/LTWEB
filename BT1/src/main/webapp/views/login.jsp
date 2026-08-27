<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập</title>
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>

<main class="auth-wrap">
    <div class="auth-card">
        <div class="card panel">
            <div class="text-center">
                <h3 class="brand">Ứng dụng của bạn</h3>
                <p class="subtitle">Đăng nhập để tiếp tục</p>
            </div>

            <div class="stack" style="margin-top:18px;">
                <% if (request.getParameter("error") != null) { %>
                <div class="alert alert-danger" role="alert">
                    Sai tên đăng nhập hoặc mật khẩu. Vui lòng thử lại.
                </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/login" method="post" data-auth-form="true" class="stack">
                    <div>
                        <label for="username" class="form-label">Tên người dùng</label>
                        <input id="username" name="username" type="text" class="form-control" placeholder="Nhập tên người dùng" required autofocus>
                    </div>

                    <div class="password-wrap">
                        <label for="password" class="form-label">Mật khẩu</label>
                        <input id="password" name="password" type="password" class="form-control" placeholder="Nhập mật khẩu" required>
                        <button type="button" id="togglePwd" class="btn-link password-toggle">Hiện</button>
                    </div>

                    <button type="submit" class="btn btn-primary ripple">Đăng nhập</button>
                </form>

                <div class="small-muted text-center">
                    Chưa có tài khoản?
                    <a class="inline-link" href="${pageContext.request.contextPath}/register">Đăng ký</a>
                </div>
            </div>
        </div>
    </div>
</main>

<script src="${pageContext.request.contextPath}/assets/js/ui.js"></script>
</body>
</html>