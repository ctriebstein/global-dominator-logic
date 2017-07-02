package org.ct.gd.logic.exception;

/**
 * Exception class for game settings
 * 
 * @author ct
 * 
 */
public class InvalidNumberOfPlayersException extends Exception {
	
	private static final long serialVersionUID = -5633144444156324657L;

	public InvalidNumberOfPlayersException(String message) {
		super(message);
	}
	
	public InvalidNumberOfPlayersException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
