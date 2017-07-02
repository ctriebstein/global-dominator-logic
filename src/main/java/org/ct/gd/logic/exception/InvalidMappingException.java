package org.ct.gd.logic.exception;

/**
 * Exception class for game settings
 * 
 * @author ct
 * 
 */
public class InvalidMappingException extends Exception {
		
	private static final long serialVersionUID = 5891833631309540509L;

	public InvalidMappingException(String message) {
		super(message);
	}
	
	public InvalidMappingException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
