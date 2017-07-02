package org.ct.gd.logic.exception;

/**
 * Exception class for game exceptions
 * 
 * @author ct
 * 
 */
public class GameException extends Exception {

	public enum GameExceptionType {
		NOT_PLAYERS_TURN, AREA_NOT_UNDER_CONTROL, AREA_NOT_ATTACKABLE, ILLEGAL_NO_OF_ARMIES, WRONG_PHASE_FOR_ACTION, ILLEGAL_CARD_COMBINATION, ILLEGAL_PATH, INVALID_ATTACK_DICE, INVALID_DEFENSE_DICE, NO_AREA_CONQUERED, MUST_TRADE_CARDS, GAME_OVER, UNKNOW_ERROR;
	}

	private static final long serialVersionUID = 5891833631309540509L;
	
	private GameExceptionType type;

	public GameException(String message, GameExceptionType type) {
		super(message + " " + type.toString());
		
		this.type = type;
	}

	public GameException(String message, GameExceptionType type, Throwable throwable) {
		super(message + " " + type.toString(), throwable);
		
		this.type = type;
	}
	
	public GameExceptionType getGameExceptionType() {
		return this.type;
	}
}
