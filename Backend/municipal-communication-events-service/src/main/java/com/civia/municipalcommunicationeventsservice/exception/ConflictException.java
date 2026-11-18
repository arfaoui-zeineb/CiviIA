package com.civia.municipalcommunicationeventsservice.exception;

/** Conflit : capacite atteinte ou inscription deja active : HTTP 409. */
public class ConflictException extends RuntimeException {

	public ConflictException(String message) {
		super(message);
	}
}
