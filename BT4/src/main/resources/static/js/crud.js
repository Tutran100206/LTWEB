/* All table/form data comes from the REST APIs. */
$(function () {
    'use strict';
    const kind = $('#management').data('kind');
    const product = kind === 'product';
    const label = product ? 'sản phẩm' : 'danh mục';
    const capital = product ? 'Product' : 'Category';
    const idField = kind + 'Id';
    const nameField = kind + 'Name';
    const imageField = product ? 'images' : 'icon';
    const endpoint = contextPath + '/api/' + kind;
    const form = document.getElementById('edit-form');
    const modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('edit-modal'));
    let previewUrl;
    let busy = false;

    function message(target, text, success) {
        $(target).removeClass('d-none alert-success alert-danger')
            .addClass(success ? 'alert-success' : 'alert-danger').text(text);
    }
    function failure(xhr, target) {
        message(target || '#page-message',
            (xhr.responseJSON && xhr.responseJSON.message) || 'Không thể kết nối máy chủ. Vui lòng thử lại.', false);
    }
    function unwrap(response) {
        if (response.status === false) throw new Error(response.message);
        return Array.isArray(response) ? response : response.body;
    }
    function imageUrl(name) { return contextPath + '/uploads/' + encodeURIComponent(name); }
    function preview(name) {
        if (previewUrl) { URL.revokeObjectURL(previewUrl); previewUrl = null; }
        $('#image-preview').toggleClass('d-none', !name).attr('src', name ? imageUrl(name) : '');
    }
    function load() {
        $('#rows').empty().append($('<tr>').append($('<td>').attr('colspan', 11).text('Đang tải...')));
        return $.getJSON(endpoint).done(function (response) {
            const list = unwrap(response);
            const tbody = $('#rows').empty();
            $('#count').text(list.length + ' ' + label);
            if (!list.length) tbody.append($('<tr>').append($('<td>').attr('colspan', 11).text('Chưa có ' + label + '.')));
            list.forEach(function (item) {
                const row = $('<tr>');
                row.append($('<td>').text(item[idField]));
                const cell = $('<td>');
                if (item[imageField]) cell.append($('<img>').attr({src: imageUrl(item[imageField]), alt: item[nameField], loading: 'lazy'}));
                else cell.text('—');
                row.append(cell, $('<td>').text(item[nameField]));
                if (product) {
                    [item.categoryName, Number(item.unitPrice).toLocaleString('vi-VN'),
                        item.discount + '%', item.quantity, item.status ? 'Đang bán' : 'Ngừng bán',
                        item.createDate ? new Date(item.createDate).toLocaleString('vi-VN') : '—'
                    ].forEach(value => row.append($('<td>').text(value)));
                }
                row.append($('<td>').append(
                    $('<button type="button" class="btn btn-sm btn-outline-primary me-2 edit">').text('Sửa').data('id', item[idField]),
                    $('<button type="button" class="btn btn-sm btn-outline-danger delete">').text('Xóa').data('id', item[idField])));
                tbody.append(row);
            });
        }).fail(function (xhr) {
            $('#rows').empty().append($('<tr>').append($('<td>').attr('colspan', 11).text('Không tải được dữ liệu.')));
            failure(xhr);
        });
    }
    function categories(selected) {
        return $.getJSON(contextPath + '/api/category').done(function (response) {
            const select = $(form.elements.categoryId).empty();
            select.append($('<option>').val('').text('Chọn danh mục'));
            const list = unwrap(response);
            list.forEach(c => select.append($('<option>').val(c.categoryId).text(c.categoryName)));
            select.val(selected == null ? '' : String(selected));
            if (!list.length) {
                message('#form-message', 'Hãy thêm danh mục trước khi thêm sản phẩm.', false);
                $('#save').prop('disabled', true);
            }
        });
    }
    function open(id) {
        form.reset();
        form.elements[idField].value = '';
        $('#form-message').addClass('d-none');
        $('#modal-title').text((id ? 'Sửa ' : 'Thêm ') + label);
        preview(null);
        $('#save').prop('disabled', true);
        modal.show();
        const request = id ? $.getJSON(endpoint + '/' + id) : $.Deferred().resolve(null).promise();
        request.then(function (response) {
            const item = response ? unwrap(response) : null;
            if (item) {
                [idField, nameField].concat(product ? ['quantity', 'unitPrice', 'discount', 'description', 'status'] : [])
                    .forEach(key => { form.elements[key].value = item[key] == null ? '' : String(item[key]); });
                preview(item[imageField]);
            }
            if (product) return categories(item && item.categoryId);
            return null;
        }).done(function () {
            $('#save').prop('disabled', product && !form.elements.categoryId.options.length);
            if (product && form.elements.categoryId.options.length === 1) $('#save').prop('disabled', true);
        }).fail(xhr => failure(xhr, '#form-message'));
    }
    $('#add').on('click', () => open(null));
    $('#refresh').on('click', load);
    $('#rows').on('click', '.edit', function () { open($(this).data('id')); });
    $('#rows').on('click', '.delete', function () {
        if (!confirm('Bạn có chắc muốn xóa ' + label + ' này?')) return;
        const button = $(this).prop('disabled', true);
        const data = {}; data[idField] = button.data('id');
        $.ajax({url: endpoint + '/delete' + capital + '?' + $.param(data), type: 'DELETE'})
            .done(function (response) { message('#page-message', response.message, true); load(); })
            .fail(xhr => failure(xhr))
            .always(() => button.prop('disabled', false));
    });
    $(form).on('submit', function (event) {
        event.preventDefault();
        if (busy || !form.reportValidity()) return;
        const updating = Boolean(form.elements[idField].value);
        const formData = new FormData(this);
        if (!updating) formData.delete(idField);
        busy = true;
        $('#save').prop('disabled', true);
        $.ajax({
            url: endpoint + '/' + (updating ? 'update' : 'add') + capital,
            type: updating ? 'PUT' : 'POST',
            data: formData, contentType: false, processData: false
        }).done(function (response) {
            busy = false; modal.hide();
            message('#page-message', response.message, true); load();
        }).fail(xhr => failure(xhr, '#form-message'))
            .always(function () { busy = false; $('#save').prop('disabled', false); });
    });
    $('#edit-modal').on('hide.bs.modal', function (event) { if (busy) event.preventDefault(); });
    $(form).find('input[type=file]').on('change', function () {
        preview(null);
        const file = this.files[0];
        if (!file) return;
        if (file.size > 5 * 1024 * 1024) {
            this.value = '';
            message('#form-message', 'Ảnh tối đa 5 MB.', false);
            return;
        }
        previewUrl = URL.createObjectURL(file);
        $('#image-preview').attr('src', previewUrl).removeClass('d-none');
    });
    load();
});
