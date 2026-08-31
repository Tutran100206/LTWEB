<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Category</title>
</head>
<body>
<h2>Edit Category</h2>

<form action="${pageContext.request.contextPath}/admin/category/update"
      method="post"
      enctype="multipart/form-data">
    <input type="hidden" name="categoryid" value="${cate.categoryid}"/>

    <div>
        <label>Category name:</label>
        <input type="text" name="categoryname" value="${cate.categoryname}" required/>
    </div>

    <div>
        <label>Current image:</label>
        <c:if test="${not empty cate.images}">
            <c:choose>
                <c:when test="${fn:startsWith(cate.images, 'http://') or fn:startsWith(cate.images, 'https://')}">
                    <img src="${cate.images}" alt="img" width="120"/>
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/image?fname=${cate.images}" alt="img" width="120"/>
                </c:otherwise>
            </c:choose>
        </c:if>
    </div>

    <div>
        <label>Link images:</label>
        <input type="text" name="images" value="${cate.images}"/>
    </div>
    <div>
        <label>Upload images:</label>
        <input type="file" name="images1" accept="image/*"/>
    </div>
    <div>
        <label>Status:</label>
        <select name="status">
            <option value="1" ${cate.status == 1 ? 'selected' : ''}>Hoạt động</option>
            <option value="0" ${cate.status != 1 ? 'selected' : ''}>Khóa</option>
        </select>
    </div>
    <div>
        <button type="submit">Update</button>
    </div>
</form>
</body>
</html>
