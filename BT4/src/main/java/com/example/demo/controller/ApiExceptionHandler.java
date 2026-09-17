package com.example.demo.controller;
import com.example.demo.dto.ApiResponse;
import org.slf4j.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(assignableTypes = {CategoryApiController.class, ProductApiController.class, ImageController.class})
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private ResponseEntity<ApiResponse<Void>> error(HttpStatusCode code, String message) {
        return ResponseEntity.status(code).body(new ApiResponse<>(false, message, null));
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> status(ResponseStatusException e) {
        return error(e.getStatusCode(), e.getReason());
    }
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> validation(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage()).distinct()
            .collect(java.util.stream.Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST, message.isBlank() ? "Dữ liệu không hợp lệ" : message);
    }
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<Void>> badInput(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "Thiếu tham số hoặc dữ liệu sai định dạng");
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> size(Exception e) {
        return error(HttpStatus.PAYLOAD_TOO_LARGE, "Ảnh tối đa 5 MB, tổng yêu cầu tối đa 6 MB");
    }
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiResponse<Void>> multipart(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "Dữ liệu upload không hợp lệ");
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> conflict(Exception e) {
        return error(HttpStatus.CONFLICT, "Dữ liệu bị trùng hoặc đang được tham chiếu, vui lòng tải lại danh sách");
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> unexpected(Exception e) {
        log.error("API request failed", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể xử lý yêu cầu, vui lòng thử lại");
    }
}
