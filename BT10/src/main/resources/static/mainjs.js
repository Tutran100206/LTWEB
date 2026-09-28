"use strict";
const tokenKey = "jwt-access-token";
const message = document.getElementById("message");
async function request(url, options = {}) {
    const response = await fetch(url, options);
    const data = await response.json();
    if (!response.ok) {
        const error = new Error(data.message || "Yêu cầu thất bại");
        error.status = response.status;
        throw error;
    }
    return data;
}
for (const action of ["login", "signup"]) {
    const form = document.getElementById(action + "-form");
    form?.addEventListener("submit", async event => {
        event.preventDefault();
        const button = form.querySelector("button");
        button.disabled = true;
        message.textContent = "";
        try {
            const data = await request("/auth/" + action, {
                method: "POST", headers: { "Content-Type": "application/json" },
                body: JSON.stringify(Object.fromEntries(new FormData(form)))
            });
            if (action === "login") {
                sessionStorage.setItem(tokenKey, data.token);
                window.location.assign("/user/profile");
            } else {
                message.textContent = "Đăng ký thành công. Bạn có thể đăng nhập.";
                form.reset();
            }
        } catch (error) { message.textContent = error.message; }
        finally { button.disabled = false; }
    });
}
function logout() {
    sessionStorage.removeItem(tokenKey);
    window.location.replace("/login");
}
document.getElementById("logout")?.addEventListener("click", logout);
if (document.getElementById("profile")) {
    const token = sessionStorage.getItem(tokenKey);
    if (!token) logout();
    else request("/users/me", { headers: { Authorization: "Bearer " + token } })
        .then(user => {
            document.getElementById("full-name").textContent = user.fullName;
            document.getElementById("email").textContent = user.email;
            document.getElementById("created-at").textContent = new Date(user.createdAt).toLocaleString("vi-VN");
            message.textContent = "";
        })
        .catch(error => { if (error.status === 401) logout(); else message.textContent = error.message; });
}
