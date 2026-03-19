package com.learning.cruddemo.exceptions;
import java.time.LocalDate;

public class ErrorResponse {
    private int status;
    private String message;
    private LocalDate timestamp;
    private String errorCode;

    public ErrorResponse(int status, String message, String errorCode) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDate.now();
    }

    public int getStatus(){
        return status;
    }

    public String getMessage(){
        return message;
    }


    public String getErrorCode() {
        return errorCode;
    }
}
