package org.ct.gd.logic.handler;

import java.io.Serializable;
import java.util.List;

import org.ct.gd.logic.GameStatus;
import org.ct.gd.logic.exception.GameException;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.AttackResult;
import org.ct.gd.logic.model.Card;
import org.ct.gd.logic.model.DefenseResult;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

/**
 * provides basic methods for game turns
 * 
 * @author ct
 * 
 */
public interface GameHandler extends Serializable {

	/**
	 * fetches the areas currently available on the board
	 * 
	 * @return a list of areas
	 */
	public AreaList getAreas();

	/**
	 * to know which players turn it is, this method returns the current player
	 * 
	 * @return the player currently in turn, null if there is none
	 */
	public Player getPlayerInTurn();

	/**
	 * at the start of the game after all areas are assigned to random players,
	 * players may place one unit to an area they control
	 * 
	 * @param player
	 *            the player currently able to place a unit
	 * @param area
	 *            the area to place the unit to
	 * @throws GameException
	 *             if it is not the players turn or initial phase
	 * 
	 */
	public void placeInitialUnit(Player player, Area area)
			throws GameException;

	/**
	 * at beginning of upkeep, player may decide to trade groups of three cards
	 * for armies. if he has 5 cards, he has to trade cards. This method should
	 * be executed in the phase TRADE_CARDS
	 * 
	 * @param player
	 *            the player who wants to trade cards. if successful, the cards
	 *            provided will be removed from the players list of cards and
	 *            put back to the game deck
	 * @param cards
	 *            cards being traded for armies - each list provided needs to
	 *            have a size of 3
	 * @return amount of armies player receives for trade
	 * @throws GameException
	 *             if card combination invalid or any list of cards traded is
	 *             not size 3 or it is not the players phase or turn
	 */
	public int tradeCards(Player player, List<List<Card>> cards)
			throws GameException;

	/**
	 * determines the amount of armies the given player is allowed to place on
	 * the board (Without cards being traded). This is used in the phase
	 * REINFORCEMENT. The amount of armies is determined by the number of areas
	 * and continents in possession and is at least 3. Formula for
	 * determination: <br>
	 * (Areas / 3) = Amount of armies (Remove all numbers behind the comma) <br>
	 * + Number of armies determined by the continents in possession: <br>
	 * -- Australia: 2 <br>
	 * -- South America: 2 <br>
	 * -- Africa: 3 <br>
	 * -- North America: 4 <br>
	 * -- Europe: 5 <br>
	 * -- Asia: 7
	 * 
	 * @param player
	 *            the player to calculate the armies for (only in his/her turn)
	 * @return the number of armies the player may use for reinforcements
	 * @throws GameException
	 *             if it is not the players turn or reinforcement phase
	 */
	// public int getAvailableReinforcements(Player player) throws GameException;

	/**
	 * Used to reinforce a certain area by a given number of armies. Used during
	 * the REINFORCEMENT phase of the players turn.
	 * 
	 * @param player
	 *            the player reinforcing one of his/her areas
	 * @param area
	 *            the area to reinforce with the number provided in the armies
	 *            param
	 * @param armies
	 *            the amount of armies to put on the given area
	 * @throws GameException
	 *             if the area to reinforce is not in the possession of the
	 *             given player or it is not the players phase or turn
	 */
	public void reinforce(Player player, Area area, int armies)
			throws GameException;

	/**
	 * executes an attack from one area to another. Those areas need to be
	 * directly connected (Neighbors), the attacking area has to be in
	 * possession of the attacking player and the defending area may not be in
	 * possession of the attacking player. This move can only be executed in the
	 * ATTACK phase of the player.
	 * 
	 * @param player
	 *            the player executing the attack
	 * @param attackingArea
	 *            the area from which the attacking player attacks
	 * @param defendingArea
	 *            the area the attacking player attacks
	 * @param numberOfDice
	 *            the number of dice the attacking player uses. It has to be <br>
	 *            - at least one <br>
	 *            - maximum three <br>
	 *            - the amount of allowed dice is determined by the number of
	 *            armies in the attacking area - 1
	 * @return an attack object containing information about the result of the
	 *         attack
	 * @throws GameException
	 *             if the player is not in his ATTACK phase or turn, if the
	 *             number of dice is invalid or if anything is wrong with the
	 *             area assignment
	 */
	public AttackResult attack(Player player, Area attackingArea,
			Area defendingArea, int numberOfDice) throws GameException;

	/**
	 * executes a defence after a successful attack by another player
	 * 
	 * @param defender
	 *            the player being in defense
	 * @param attack
	 *            the result of the attacking players attack
	 * @param numberOfDice
	 *            the number of dice used for defense purposes: <br>
	 *            - at least 1 <br>
	 *            - maximum two <br>
	 *            - number of dice is determined by the amount of armies in the
	 *            area
	 * @return the result of the defenders defense
	 * @throws GameException
	 *             if the number of dice is invalid or if anything is wrong with
	 *             the area assignment
	 */
	public DefenseResult defend(Player defender, AttackResult attack,
			int numberOfDice) throws GameException;

	/**
	 * after a successful attack with conquest of the attacked area, the
	 * attacking player might move any army (but one) from the attacking area to
	 * the newly possessed area.
	 * 
	 * @param attacker
	 *            the player moving his/her armies
	 * @param defense
	 *            the defense result containing all armies still available for
	 *            moving (attackingDestination - attackerLosses) to the
	 *            defending area
	 * @param numberOfArmies
	 *            the number of armies the player wants to move
	 * @throws GameException
	 *             if the player is not in his ATTACK phase or turn, if the
	 *             number of armies is invalid, if the player already moved on
	 *             to a new attack or if anything is wrong with the area
	 *             assignment
	 */
	public void moveArmiesAfterConquest(Player attacker, DefenseResult defense,
			int numberOfArmies) throws GameException;

	/**
	 * If the player conquered at least one area in his/her turn, a card will be
	 * drawn from the card deck. Only once available in the DRAW_CARD phase of
	 * the turn
	 * 
	 * @param player
	 *            the player being able to draw a card
	 * @return a card out of the stack of available cards
	 * @throws GameException
	 *             if no card is available (should never happen) or the player
	 *             may not draw any cards (no conquest this turn, already drew,
	 *             etc.)
	 */
	public Card drawCard(Player player) throws GameException;

	/**
	 * At the end of each turn, a player may move armies from one area (Source)
	 * to another area (destination) that is either directly connected to the
	 * source or connected through areas that are in possession of the current
	 * player. Only once available in the FORTIFICATION phase.
	 * 
	 * @param player
	 *            the player moving armies from one area to the other
	 * @param source
	 *            the area providing the armies to move
	 * @param destination
	 *            the destination receiving the moved armies
	 * @throws GameException
	 *             if the player is not in his FORTIFICATION phase or turn,
	 *             armies were already moved, the amount of armies is invalid or
	 *             one of the areas is not under the players control
	 */
	public void fortify(Player player, Area source, Area destination,
			int numberOfArmies) throws GameException;

	/**
	 * At the end of phase the player must confirm that his/her phase ended.
	 * Only available in the ATTACK, FORTIFY, REINFORCE and CONFIRM phase
	 * 
	 * @param player
	 *            the player wanting to end the turn
	 * @throws GameException
	 *             if the player is not in the CONFIRM phase or turn or if the
	 *             game is over
	 */
	public void confirmEndOfPhase(Player player) throws GameException;

	/**
	 * get the status of the game. will return RUNNING if no goal has been met
	 * yet. If a goal was met GAME_OVER will be returned
	 * 
	 * @return GameStatus RUNNING if the game is running or GAME_OVER if a
	 *         player reached his goal
	 */
	public GameStatus getGameStatus();

	/**
	 * gets the player that won the game
	 * 
	 * @return if a player has already met his goal this player will be returned
	 *         here. null otherwise
	 */
	public Player getWinner();
}
