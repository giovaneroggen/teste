package com.roggen.voting.config.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class GenericException extends RuntimeException{

    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    private String error;

    public GenericException(String message){
        super(message);
        this.error = message;
    }

    public GenericException(String message, HttpStatus responseStatus) {
        super(message);
        this.error = message;
        this.status = responseStatus;
    }

}
