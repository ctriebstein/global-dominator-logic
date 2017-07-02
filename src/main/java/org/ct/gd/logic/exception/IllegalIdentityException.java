package org.ct.gd.logic.exception;

/**
 * Exception class for game settings
 * 
 * @author ct
 * 
 */
public class IllegalIdentityException extends Exception {
		
	private static final long serialVersionUID = 5891833631309540509L;

	public IllegalIdentityException(String message) {
		super(message);
	}
	
	public IllegalIdentityException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
