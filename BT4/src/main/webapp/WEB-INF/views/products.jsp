<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="layout/header.jsp" %>
<section id="management" data-kind="product">
<div class="d-flex justify-content-between align-items-center mb-3">
    <div><h1 class="h3">Quản lý sản phẩm</h1><span id="count" class="text-secondary"></span></div>
    <div><button type="button" id="refresh" class="btn btn-outline-secondary">Tải lại</button>
    <button type="button" id="add" class="btn btn-primary">Thêm sản phẩm</button></div>
</div>
<div class="table-responsive bg-white rounded shadow-sm">
<table class="table table-hover mb-0">
<thead><tr><th scope="col">ID</th><th scope="col">Ảnh</th><th scope="col">Tên sản phẩm</th><th scope="col">Danh mục</th><th scope="col">Đơn giá (đ)</th><th scope="col">Giảm giá</th><th scope="col">Số lượng</th><th scope="col">Trạng thái</th><th scope="col">Ngày tạo</th><th scope="col">Thao tác</th></tr></thead>
<tbody id="rows"></tbody>
</table>
</div>
</section>
<div class="modal fade" id="edit-modal" tabindex="-1" aria-labelledby="modal-title" aria-hidden="true">
<div class="modal-dialog modal-dialog-scrollable"><div class="modal-content">
<form id="edit-form" enctype="multipart/form-data">
    <div class="modal-header"><h2 id="modal-title" class="modal-title fs-5"></h2><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button></div>
    <div class="modal-body">
        <div id="form-message" class="alert d-none" role="alert"></div>
        <input type="hidden" name="productId">
        <div class="mb-3"><label class="form-label" for="productName">Tên sản phẩm</label><input class="form-control" id="productName" name="productName" type="text" required maxlength="150"></div>
        <div class="mb-3"><label class="form-label" for="quantity">Số lượng</label><input class="form-control" id="quantity" name="quantity" type="number" required min="0" max="2147483647" step="1" value="0"></div>
        <div class="mb-3"><label class="form-label" for="unitPrice">Đơn giá (đ)</label><input class="form-control" id="unitPrice" name="unitPrice" type="number" required min="0" step="0.01" value="0"></div>
        <div class="mb-3"><label class="form-label" for="discount">Giảm giá (%)</label><input class="form-control" id="discount" name="discount" type="number" required min="0" max="100" step="0.01" value="0"></div>
        <div class="mb-3"><label for="categoryId" class="form-label">Danh mục</label><select class="form-select" id="categoryId" name="categoryId" required></select></div>
        <div class="mb-3"><label for="status" class="form-label">Trạng thái</label><select class="form-select" id="status" name="status" required><option value="true">Đang bán</option><option value="false">Ngừng bán</option></select></div>
        <div class="mb-3"><label for="description" class="form-label">Mô tả</label><textarea class="form-control" id="description" name="description" maxlength="4000"></textarea></div>
        <div class="mb-3"><label class="form-label" for="imageFile">Ảnh sản phẩm</label><input class="form-control" id="imageFile" name="imageFile" type="file" accept="image/png,image/jpeg,image/gif"></div>
        <p class="form-text">PNG, JPEG, GIF tối đa 5 MB. Không chọn ảnh mới sẽ giữ ảnh hiện tại.</p>
        <img id="image-preview" class="image-preview d-none" alt="Ảnh xem trước">
    </div>
    <div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button><button type="submit" id="save" class="btn btn-primary">Lưu</button></div>
</form>
</div></div>
</div>
<%@ include file="layout/footer.jsp" %>
