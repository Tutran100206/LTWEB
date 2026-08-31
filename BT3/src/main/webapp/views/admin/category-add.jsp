<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Category</title>
</head>
<body>
<h2>Add Category</h2>

<form action="${pageContext.request.contextPath}/admin/category/insert"
      method="post"
      enctype="multipart/form-data">
    <div>
        <label>Category name:</label>
        <input type="text" name="categoryname" required/>
    </div>
    <div>
        <label>Link images:</label>
        <input type="text" name="images"/>
    </div>
    <div>
        <label>Upload images:</label>
        <input type="file" name="images1" accept="image/*"/>
    </div>
    <div>
        <label>Status:</label>
        <select name="status">
            <option value="1">Hoạt động</option>
            <option value="0">Khóa</option>
        </select>
    </div>
    <div>
        <button type="submit">Insert</button>
    </div>
</form>
</body>
</html>
