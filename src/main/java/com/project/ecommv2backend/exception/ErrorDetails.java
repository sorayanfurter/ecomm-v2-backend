package com.project.ecommv2backend.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDetails {
    private String status;
    private String message;

    public ErrorDetails(String status, String message) {
        this.status = status;
        this.message = message;
    }

}