package com.example.vidu3.controller;

import com.example.vidu3.service.BusinessException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class) public ModelAndView business(BusinessException ex) { return error(HttpStatus.BAD_REQUEST,ex.getMessage()); }
    @ExceptionHandler(ResponseStatusException.class) public ModelAndView missing(ResponseStatusException ex) { return error(HttpStatus.valueOf(ex.getStatusCode().value()),ex.getReason()==null?"Không tìm thấy nội dung yêu cầu.":ex.getReason()); }
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class) public ModelAndView denied() { var view=new ModelAndView("access-denied"); view.setStatus(HttpStatus.FORBIDDEN); return view; }
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class) public ModelAndView upload() { return error(HttpStatus.CONTENT_TOO_LARGE,"Ảnh quá lớn. Vui lòng chọn ảnh tối đa 5 MB."); }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class) public ModelAndView conflict() { return error(HttpStatus.CONFLICT,"Dữ liệu trùng hoặc đang được sử dụng. Vui lòng tải lại trang và thử lại."); }
    @ExceptionHandler({org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class, org.springframework.web.bind.MissingServletRequestParameterException.class})
    public ModelAndView invalid() { return error(HttpStatus.BAD_REQUEST,"Tham số yêu cầu không hợp lệ."); }
    private ModelAndView error(HttpStatus status,String message) { var view=new ModelAndView("error"); view.setStatus(status); view.addObject("errorMessage",message); return view; }
}
