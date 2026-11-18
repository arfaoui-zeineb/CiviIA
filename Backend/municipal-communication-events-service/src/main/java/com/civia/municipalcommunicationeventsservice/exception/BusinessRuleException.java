package com.civia.municipalcommunicationeventsservice.exception;

/** Regle metier violee : HTTP 422. */
public class BusinessRuleException extends RuntimeException {

	public BusinessRuleException(String message) {
		super(message);
	}
}
