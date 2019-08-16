package com.roggen.voting.config;

import com.roggen.voting.config.exception.GenericException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RestControllerAdvice
public class ControllerAdviceConfiguration {

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<Response> genericHandler(GenericException e){
        final HttpStatus status = e.getStatus();
        log.error("ControllerAdviceConfiguration.genericHandler={}", e);
        final Response response = new Response(status.getReasonPhrase(), status.value(), e.getError(), null);
        return ResponseEntity.status(status)
                             .body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("ControllerAdviceConfiguration.handleMethodArgumentNotValidException={}", e);
        return new Response(status.getReasonPhrase(), status.value(), e.getMessage(), collectFieldErrors(e));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Response handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("ControllerAdviceConfiguration.handleException={}", e);
        return new Response(status.getReasonPhrase(), status.value(), e.getMessage(), null);
    }

    private List<ErrorInfo> collectFieldErrors(MethodArgumentNotValidException e) {
        return e.getBindingResult()
                .getFieldErrors()
                .stream().map(fe -> new ErrorInfo(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
                .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    public static class Response{
        private String status;
        private Integer code;
        private String error;
        private List<ErrorInfo> errors;
    }

    @Data
    @AllArgsConstructor
    public class ErrorInfo {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
