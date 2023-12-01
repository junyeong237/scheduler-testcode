package com.example.scheduler.exception;

import com.example.scheduler.dto.ResponseEntityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice

@Slf4j
public class ExceptionAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntityDto<?> illegalArgumentExceptionAdvice(IllegalArgumentException e) {
        return new ResponseEntityDto<>(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntityDto<?> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        // 에러 메시지를 가져와 적절한 응답을 생성할 수 있습니다.
        for (FieldError fieldError : fieldErrors) {
            log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
        }

        return new ResponseEntityDto<>(HttpStatus.BAD_REQUEST,"Validation 오류로 회원가입이 안됩니다",null);
    }


}
