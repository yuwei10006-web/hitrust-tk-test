package com.example.tk_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** DTO 上的 @NotBlank/@Size/@Pattern 等驗證失敗 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst()
                .orElse("參數格式錯誤");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    /** toolkit.transaction() 內部拋出的例外（連線逾時、憑證錯誤等），統一在這裡收 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOther(Exception e) {
        log.error("交易處理發生未預期例外", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "交易處理失敗，請稍後再試"));
    }
}
