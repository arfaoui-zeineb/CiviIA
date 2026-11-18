package com.civicpulse.communication.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends BusinessException {
    public BusinessRuleException(String message, String code) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, code);
    }

    public BusinessRuleException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_RULE_VIOLATION");
    }
}

