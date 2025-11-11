package com.prashant.queuectl.exception;

public class JobExecutionException extends RuntimeException {
    public JobExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
