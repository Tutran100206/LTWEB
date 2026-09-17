"""Integration checks against the running app; only removes records created by this script."""
import base64
import json
import os
import urllib.error
import urllib.request
import uuid

BASE = os.environ.get("TEST_BASE_URL", "http://localhost:8080").rstrip("/")
PNG = base64.b64decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+aJ1sAAAAASUVORK5CYII=")
checks = 0


def request(method, path, expected=200, fields=None, file=None):
    global checks
    headers = {}
    data = None
    if fields is not None:
        boundary = "BT4" + uuid.uuid4().hex
        parts = []
        for key, value in fields.items():
            parts.append(('--' + boundary + '\r\nContent-Disposition: form-data; name="' + key
                          + '"\r\n\r\n' + str(value) + '\r\n').encode("utf-8"))
        if file:
            key, content = file
            parts.append(('--' + boundary + '\r\nContent-Disposition: form-data; name="' + key
                          + '"; filename="test.png"\r\nContent-Type: image/png\r\n\r\n').encode()
                         + content + b'\r\n')
        parts.append(('--' + boundary + '--\r\n').encode())
        data = b''.join(parts)
        headers["Content-Type"] = "multipart/form-data; boundary=" + boundary
    req = urllib.request.Request(BASE + path, data=data, headers=headers, method=method)
    try:
        response = urllib.request.urlopen(req, timeout=30)
    except urllib.error.HTTPError as error:
        response = error
    with response:
        payload = response.read()
        assert response.status == expected, (method, path, response.status, payload[:1000])
        content_type = response.headers.get("Content-Type", "")
        result = json.loads(payload) if "json" in content_type else payload
    checks += 1
    if expected >= 400 and isinstance(result, dict):
        assert result["status"] is False and result["message"]
    return result


def main():
    token = uuid.uuid4().hex
    category_ids, product_ids = [], []
    try:
        for page in ["/admin/categories", "/admin/products"]:
            html = request("GET", page).decode()
            assert 'id="rows"' in html and html.count("jquery-3.6.4.min.js") == 1
        request("GET", "/swagger-ui.html")
        spec = request("GET", "/v3/api-docs")
        for entity, capital in [("category", "Category"), ("product", "Product")]:
            for path in ["", "/{id}", "/add" + capital, "/update" + capital, "/delete" + capital]:
                assert "/api/" + entity + path in spec["paths"]
            request("GET", "/api/" + entity + "/9223372036854775807", 404)
            request("GET", "/api/" + entity + "/abc", 400)
        request("POST", "/api/category/addCategory", 400, {"categoryName": " "})
        c = request("POST", "/api/category/addCategory", 201,
                    {"categoryName": "Smoke " + token}, ("icon", PNG))["body"]
        cid = c["categoryId"]
        category_ids.append(cid)
        request("GET", "/uploads/" + c["icon"])
        request("POST", "/api/category/addCategory", 409, {"categoryName": c["categoryName"]})
        edited = request("PUT", "/api/category/updateCategory", 200,
                         {"categoryId": cid, "categoryName": c["categoryName"] + " edited"})["body"]
        assert edited["icon"] == c["icon"]
        replaced = request("PUT", "/api/category/updateCategory", 200,
                           {"categoryId": cid, "categoryName": edited["categoryName"]}, ("icon", PNG))["body"]
        assert replaced["icon"] != c["icon"]
        request("GET", "/uploads/" + c["icon"], 404)
        request("GET", "/uploads/" + replaced["icon"])
        second = request("POST", "/api/category/addCategory", 201,
                         {"categoryName": "Other " + token})["body"]
        category_ids.append(second["categoryId"])
        request("PUT", "/api/category/updateCategory", 409,
                {"categoryId": second["categoryId"], "categoryName": edited["categoryName"]})
        data = {"productName": "Smoke " + token, "quantity": 2, "unitPrice": "12345.50",
                "discount": "10.25", "description": "Test tiếng Việt <script>",
                "categoryId": cid, "status": "true"}
        for key, value in [("quantity", -1), ("unitPrice", -1), ("discount", 101),
                           ("productName", " "), ("status", "invalid"), ("quantity", "abc")]:
            request("POST", "/api/product/addProduct", 400, dict(data, **{key: value}))
        request("POST", "/api/product/addProduct", 404, dict(data, categoryId=9223372036854775807))
        request("POST", "/api/product/addProduct", 400, data, ("imageFile", b"not an image"))
        p = request("POST", "/api/product/addProduct", 201, data, ("imageFile", PNG))["body"]
        pid = p["productId"]
        product_ids.append(pid)
        assert p["categoryId"] == cid and p["categoryName"] == edited["categoryName"]
        assert p["description"] == data["description"] and p["status"] is True
        request("GET", "/uploads/" + p["images"])
        request("DELETE", "/api/category/deleteCategory?categoryId=" + str(cid), 409)
        get = request("GET", "/api/product/" + str(pid))["body"]
        assert get["images"] == p["images"]
        data.update(productId=pid, categoryId=second["categoryId"], status="false", quantity=0)
        updated = request("PUT", "/api/product/updateProduct", 200, data)["body"]
        assert updated["images"] == p["images"] and updated["createDate"] == p["createDate"]
        assert updated["status"] is False and updated["categoryId"] == second["categoryId"]
        changed = request("PUT", "/api/product/updateProduct", 200, data, ("imageFile", PNG))["body"]
        assert changed["images"] != p["images"] and changed["createDate"] == p["createDate"]
        request("GET", "/uploads/" + p["images"], 404)
        request("GET", "/uploads/" + changed["images"])
        for entity in ["product", "category"]:
            assert isinstance(request("GET", "/api/" + entity)["body"], list)
        request("PUT", "/api/product/updateProduct", 404, dict(data, productId=9223372036854775807))
        request("PUT", "/api/product/updateProduct", 404, dict(data, categoryId=9223372036854775807))
        request("PUT", "/api/category/updateCategory", 404,
                {"categoryId": 9223372036854775807, "categoryName": "Missing"})
        request("DELETE", "/api/product/deleteProduct?productId=" + str(pid))
        product_ids.remove(pid)
        request("GET", "/uploads/" + changed["images"], 404)
        request("DELETE", "/api/product/deleteProduct?productId=" + str(pid), 404)
        for cid in category_ids[:]:
            request("DELETE", "/api/category/deleteCategory?categoryId=" + str(cid))
            category_ids.remove(cid)
        request("GET", "/uploads/" + replaced["icon"], 404)
        request("DELETE", "/api/category/deleteCategory?categoryId=" + str(cid), 404)
        print("PASS:", checks, "HTTP checks (SQL Server, multipart POST/PUT, CRUD, validation, JSP, Swagger)")
    finally:
        for pid in product_ids:
            request("DELETE", "/api/product/deleteProduct?productId=" + str(pid))
        for cid in category_ids:
            request("DELETE", "/api/category/deleteCategory?categoryId=" + str(cid))


if __name__ == "__main__":
    main()
