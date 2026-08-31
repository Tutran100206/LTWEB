<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>Category List</title>
</head>
<body>
<h2>Category List</h2>
<p>
    <a href="${pageContext.request.contextPath}/admin/category/add">Add Category</a>
</p>

<table border="1" cellpadding="8" cellspacing="0">
    <tr>
        <th>STT</th>
        <th>Images</th>
        <th>Category name</th>
        <th>Status</th>
        <th>Action</th>
    </tr>
    <c:forEach items="${listcate}" var="cate" varStatus="stt">
        <tr>
            <td>${stt.index + 1}</td>
            <td>
                <c:if test="${not empty cate.images}">
                    <c:choose>
                        <c:when test="${fn:startsWith(cate.images, 'http://') or fn:startsWith(cate.images, 'https://')}">
                            <img src="${cate.images}" alt="img" width="100"/>
                        </c:when>
                        <c:otherwise>
                            <img src="${pageContext.request.contextPath}/image?fname=${cate.images}" alt="img" width="100"/>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </td>
            <td>${cate.categoryname}</td>
            <td>
                <c:if test="${cate.status == 1}">Hoạt động</c:if>
                <c:if test="${cate.status != 1}">Khóa</c:if>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/category/edit?id=${cate.categoryid}">Sửa</a>
                |
                <a href="${pageContext.request.contextPath}/admin/category/delete?id=${cate.categoryid}"
                   onclick="return confirm('Bạn chắc chắn muốn xóa?');">Xóa</a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
