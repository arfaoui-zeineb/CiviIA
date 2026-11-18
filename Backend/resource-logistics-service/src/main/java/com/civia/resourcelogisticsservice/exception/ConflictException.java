package com.civia.resourcelogisticsservice.exception;

/** Conflit de reservation (chevauchement d'intervalles) : HTTP 409. */
public class ConflictException extends RuntimeException {

	public ConflictException(String message) {
		super(message);
	}
}
