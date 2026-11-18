package com.civia.resourcelogisticsservice.exception;

/** Regle metier violee (ressource indisponible, operation interdite) : HTTP 422. */
public class BusinessRuleException extends RuntimeException {

	public BusinessRuleException(String message) {
		super(message);
	}
}
