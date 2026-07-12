package com.example.miniewallet.common.exception;

public class DuplicateRequestException extends RuntimeException {

    public DuplicateRequestException(String referenceId) {
        super("Reference id already used with different parameters: " + referenceId);
    }
}
