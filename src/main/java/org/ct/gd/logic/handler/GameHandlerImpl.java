package org.ct.gd.logic.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.ct.gd.logic.GameStatus;
import org.ct.gd.logic.exception.GameException;
import org.ct.gd.logic.exception.GameException.GameExceptionType;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.AttackResult;
import org.ct.gd.logic.model.Card;
import org.ct.gd.logic.model.Continent;
import org.ct.gd.logic.model.DefenseResult;
import org.ct.gd.logic.model.Phase;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

/**
 * class providing all necessary game operations such as attack, defend, fortify, etc.
 * 
 * @author ct
 */
public class GameHandlerImpl implements GameHandler {

	private static final long serialVersionUID = 31190490102931707L;

	private static final int NO_OF_CARDS_TO_TRADE = 3;
	private static final int MAX_CARDS_IN_HAND_BEFORE_TRADE = 5;

	// Constants for continental reinforcements
	private static final int REINFORCEMENTS_AUSTRALIA = 2;
	private static final int REINFORCEMENTS_SOUTH_AMERICA = 2;
	private static final int REINFORCEMENTS_AFRICA = 3;
	private static final int REINFORCEMENTS_NORTH_AMERICA = 4;
	private static final int REINFORCEMENTS_EUROPE = 5;
	private static final int REINFORCEMENTS_ASIA = 7;

	private static final int REINFORCEMENT_DETERMINATION_FACTOR = 3;
	private static final int MINIMUM_REINFORCEMENTS_PER_TURN = 3;

	/**
	 * number of eyes the game dices have
	 */
	private static final int DICE_EYES = 6;
	private static final int MAXIMUM_ATTACK_DICE = 3;
	private static final int MAXIMUM_DEFENSE_DICE = 2;
	private static final int MINIMUM_ATTACK_OR_DEFENSE_DICE = 1;
	
	// constant for goals
	private static final int CONTROLLED_AREAS_FOR_VICTORY = 24;

	/**
	 * The amount of armies returned for a trade increases every time cards are
	 * traded. The initial values are:<br>
	 * Trade 1: 4 Armies<br>
	 * Trade 2: 6 Armies<br>
	 * and so forth (See array constants)
	 */
	private static final int[] RETURNED_ARMIES_FOR_TRADE = new int[] { 4, 6, 8, 10, 12, 15 };
	/**
	 * If the above array is out of bounds (because more trades took place
	 * already), the amount of armies returned is always increased by this
	 * constant
	 */
	private static final int FACTOR_AFTER_SURPASSING_ARRAY = 5;

	/**
	 * the amount of armies returned for a trade. This value is determined by
	 * the constants RETURNED_ARMIES_FOR_TRADE and FACTOR_AFTER_SURPASSING_ARRAY
	 * (see comments above)
	 */
	private int armiesReturned = RETURNED_ARMIES_FOR_TRADE[0];

	/**
	 * the deck of cards available
	 */
	private List<Card> availableCards;

	/**
	 * all areas available on the board
	 */
	private AreaList allAreas;

	/**
	 * all the players in the game
	 */
	private List<Player> players;
	
	/**
	 * the one and only winner of the game
	 */
	private Player winner = null;
	
	/**
	 * the status of the game keeps being RUNNING until somebody won the game
	 */
	private GameStatus gameStatus = GameStatus.RUNNING;

	/**
	 * basic enetry point after the game was started. providing all required information
	 * 
	 * @param availableCards
	 * 					the card deck available with cards that are distributed to players 
	 * 					after conquering areas
	 * @param allAreas
	 * 					a list of areas that can be occupied by players (and are preoccupied
	 * 					by a random player)
	 * @param players
	 * 					a list of players participating in this game
	 */
	public GameHandlerImpl(List<Card> availableCards, AreaList allAreas, List<Player> players) {
		if (availableCards == null) {
			this.availableCards = new ArrayList<>();
		} else {
			this.availableCards = availableCards;
		}

		this.allAreas = allAreas;
		this.players = players;
	}

	@Override
	public Player getPlayerInTurn() {
		for (Player p : this.players) {
			if (p.isTurn()) {
				return p;
			}
		}

		return null;
	}

	@Override
	public void placeInitialUnit(Player player, Area area) throws GameException {
		verifyTurn(player);

		if (player.getPhase() != Phase.INITIAL_PLACEMENT) {
			throw new GameException("Not players initial placement phase", GameExceptionType.WRONG_PHASE_FOR_ACTION);
		}

		if (area == null || !area.getControllingPlayer().equals(player)) {
			throw new GameException("Player may not reinforce areas not under control", GameExceptionType.AREA_NOT_UNDER_CONTROL);
		}

		if (player.getNoOfReinforcements() > 0) {
			area.setArmies(area.getArmies() + 1);
			player.setNoOfReinforcements(player.getNoOfReinforcements() - 1);
		}

		// set next player - if there are no more armies to place, the next player may start the first turn
		boolean moreArmiesToPlace = false;
		for (Player p : this.players) {
			if (p.getPhase() == Phase.INITIAL_PLACEMENT && p.getNoOfReinforcements() > 0) {
				moreArmiesToPlace = true;
			}
		}
		
		setNextPlayersTurn(!moreArmiesToPlace);
	}

	@Override
	public int tradeCards(Player player, List<List<Card>> cards) throws GameException {
		verifyTurn(player);

		if (player.getPhase() != Phase.TRADE_CARDS) {
			throw new GameException("Not players trading phase", GameExceptionType.WRONG_PHASE_FOR_ACTION);
		}

		// no cards given means exception is thrown
		if (cards == null || this.availableCards == null) {
			throw new GameException("No cards given for trading or deck null", GameExceptionType.ILLEGAL_CARD_COMBINATION);
		}

		int retVal = 0;

		// check each card list
		for (List<Card> cardList : cards) {
			// number of cards
			if (cardList == null || cardList.size() != NO_OF_CARDS_TO_TRADE) {
				throw new GameException("Cards provided don't match the required number of cards for trading", GameExceptionType.ILLEGAL_CARD_COMBINATION);
			}

			if (!verifyCardCombination(cardList)) {
				throw new GameException("The given card combination is invalid", GameExceptionType.ILLEGAL_CARD_COMBINATION);
			}
		}

		// second iterator in case no exception occured
		for (List<Card> cardList : cards) {
			// restock deck
			this.availableCards.addAll(cardList);
			retVal += armiesReturned;

			// assign return value and increase armiesReturned value
			boolean foundNumberInArray = false;
			for (int i = 0; i < RETURNED_ARMIES_FOR_TRADE.length; i++) {
				if (RETURNED_ARMIES_FOR_TRADE[i] == armiesReturned) {
					foundNumberInArray = true;

					if ((i + 1) >= RETURNED_ARMIES_FOR_TRADE.length) {
						armiesReturned += FACTOR_AFTER_SURPASSING_ARRAY;
					} else {
						armiesReturned = RETURNED_ARMIES_FOR_TRADE[i + 1];
					}
					break;
				}
			}
			if (!foundNumberInArray) {
				armiesReturned += FACTOR_AFTER_SURPASSING_ARRAY;
			}
		}
		
		player.setNoOfReinforcements(retVal);

		return retVal;
	}

	public int getAvailableReinforcements(Player player) throws GameException {
		verifyTurn(player);

		if (player.getPhase() != Phase.REINFORCEMENT) {
			throw new GameException("Not players reinforcement phase", GameExceptionType.WRONG_PHASE_FOR_ACTION);
		}

		int noOfArmies = 0;

		// determine number of areas this player controls
		int armiesGrantedForControlledAreas = this.allAreas.getNumberOfAreasControlledByPlayer(player) / REINFORCEMENT_DETERMINATION_FACTOR;

		if (armiesGrantedForControlledAreas < MINIMUM_REINFORCEMENTS_PER_TURN) {
			armiesGrantedForControlledAreas = MINIMUM_REINFORCEMENTS_PER_TURN;
		}

		noOfArmies += armiesGrantedForControlledAreas;

		// for each continent in control increase the number of reinforcements
		// accordingly
		if (this.allAreas.controlsContinent(Continent.AUSTRALIA, player)) {
			noOfArmies += REINFORCEMENTS_AUSTRALIA;
		}
		if (this.allAreas.controlsContinent(Continent.SOUTH_AMERICA, player)) {
			noOfArmies += REINFORCEMENTS_SOUTH_AMERICA;
		}
		if (this.allAreas.controlsContinent(Continent.AFRICA, player)) {
			noOfArmies += REINFORCEMENTS_AFRICA;
		}
		if (this.allAreas.controlsContinent(Continent.NORTH_AMERICA, player)) {
			noOfArmies += REINFORCEMENTS_NORTH_AMERICA;
		}
		if (this.allAreas.controlsContinent(Continent.EUROPE, player)) {
			noOfArmies += REINFORCEMENTS_EUROPE;
		}
		if (this.allAreas.controlsContinent(Continent.ASIA, player)) {
			noOfArmies += REINFORCEMENTS_ASIA;
		}
		
		player.setNoOfReinforcements(player.getNoOfReinforcements() + noOfArmies);

		return noOfArmies;
	}

	@Override
	public void reinforce(Player player, Area area, int armies) throws GameException {
		verifyTurn(player);

		if (player.getPhase() != Phase.REINFORCEMENT) {
			throw new GameException("Not players reinforcement phase", GameExceptionType.WRONG_PHASE_FOR_ACTION);
		}

		if (area == null || !area.getControllingPlayer().equals(player)) {
			throw new GameException("Player may not reinforce areas not under control", GameExceptionType.AREA_NOT_UNDER_CONTROL);
		}

		if (armies <= 0 || armies > player.getNoOfReinforcements()) {
			throw new GameException("Invalid reinforcements given. Available: " + player.getNoOfReinforcements(), GameExceptionType.ILLEGAL_NO_OF_ARMIES);
		}

		area.setArmies(area.getArmies() + armies);
		player.setNoOfReinforcements(player.getNoOfReinforcements() - armies);
	}

	@Override
	public AttackResult attack(Player player, Area attackingArea, Area defendingArea, int numberOfDice) throws GameException {
		verifyTurn(player);

		if (player.getPhase() != Phase.ATTACK) {
			throw new GameException("Not players attack phase", GameExceptionType.WRONG_PHASE_FOR_ACTION);
		}

		if (attackingArea == null || !attackingArea.getControllingPlayer().equals(player)) {
			throw new GameException("Player may not attack from this area", GameExceptionType.AREA_NOT_UNDER_CONTROL);
		}

		if (defendingArea == null || defendingArea.getControllingPlayer().equals(player)) {
			throw new GameException("Player controls this area - not attackable by himself", GameExceptionType.AREA_NOT_ATTACKABLE);
		}

		if (!attackingArea.getNeighbours().contains(defendingArea)) {
			throw new GameException("Areas are not neighbours - one cannot attack the other", GameExceptionType.AREA_NOT_ATTACKABLE);
		}

		if (numberOfDice > MAXIMUM_ATTACK_DICE || numberOfDice < MINIMUM_ATTACK_OR_DEFENSE_DICE) {
			throw new GameException("Number of attack dice have to be between " + MINIMUM_ATTACK_OR_DEFENSE_DICE + " or " + MAXIMUM_ATTACK_DICE,
					GameExceptionType.INVALID_ATTACK_DICE);
		}

		if (attackingArea.getArmies() - numberOfDice < 1) {
			throw new GameException("Number of dice may only be (number of armies in field) - 1", GameExceptionType.INVALID_ATTACK_DICE);
		}

		// roll the dice
		Integer[] rolledDice = new Integer[numberOfDice];
		Random rd = new Random();
		for (int i = 0; i < rolledDice.length; i++) {
			rolledDice[i] = rd.nextInt(DICE_EYES) + 1;
		}

		Arrays.sort(rolledDice, Collections.reverseOrder());

		return new AttackResult(attackingArea, defendingArea, rolledDice);
	}

	@Override
	public DefenseResult defend(Player defender, AttackResult attack, int numberOfDice) throws GameException {
		if (attack == null || !attack.getDefendingArea().getControllingPlayer().equals(defender)) {
			throw new GameException("Wrong player defending area", GameExceptionType.AREA_NOT_UNDER_CONTROL);
		}

		if (numberOfDice > MAXIMUM_DEFENSE_DICE || numberOfDice < MINIMUM_ATTACK_OR_DEFENSE_DICE) {
			throw new GameException("Number of defense dice have to be between " + MINIMUM_ATTACK_OR_DEFENSE_DICE + " or " + MAXIMUM_DEFENSE_DICE,
					GameExceptionType.INVALID_DEFENSE_DICE);
		}

		if (attack.getDefendingArea().getArmies() - numberOfDice < 0) {
			throw new GameException("Number of dice may only be (number of armies in field)", GameExceptionType.INVALID_DEFENSE_DICE);
		}

		// roll the dice
		Integer[] rolledDice = new Integer[numberOfDice];
		Random rd = new Random();
		for (int i = 0; i < rolledDice.length; i++) {
			rolledDice[i] = rd.nextInt(DICE_EYES) + 1;
		}

		Arrays.sort(rolledDice, Collections.reverseOrder());

		int lostAttackArmies = 0;
		int lostDefenseArmies = 0;
		boolean hasConqueredArea = false;
		// compare to attack values and update
		for (int i = 0; i < attack.getAttackValues().length; i++) {
			// if there are no more defending dice
			if (rolledDice.length < (i + 1)) {
				break;
			}

			if (rolledDice[i] < attack.getAttackValues()[i]) {
				attack.getDefendingArea().setArmies(attack.getDefendingArea().getArmies() - 1);
				lostDefenseArmies++;
			} else {
				attack.getAttackingArea().setArmies(attack.getAttackingArea().getArmies() - 1);
				lostAttackArmies++;
			}
		}

		// in case the area was conquered set new controlling player and move
		// armies
		if (attack.getDefendingArea().getArmies() == 0) {
			attack.getDefendingArea().setControllingPlayer(attack.getAttackingArea().getControllingPlayer());
			attack.getDefendingArea().setArmies(attack.getAttackValues().length - lostAttackArmies);
			attack.getAttackingArea().setArmies(attack.getAttackingArea().getArmies() - (attack.getAttackValues().length - lostAttackArmies));
			attack.getAttackingArea().getControllingPlayer().setConqueredAreaThisTurn(true);
			hasConqueredArea = true;
			
			// check if the losing player has any areas left. if not, remove him from the game
			if (this.allAreas.getNumberOfAreasControlledByPlayer(defender) == 0) {
				// give all cards to winning player
				Player attacker = attack.getAttackingArea().getControllingPlayer();
				attacker.getCards().addAll(defender.getCards());
				defender.getCards().clear();
				this.players.remove(defender);
				
				// check if a player won the game
				if (this.players.size() == 1) {
					this.gameStatus = GameStatus.GAME_OVER;
					this.winner = this.players.get(0);
				}
				
				if (attacker.getCards().size() > MAX_CARDS_IN_HAND_BEFORE_TRADE) {
					attacker.setPhase(Phase.TRADE_CARDS_AFTER_DEFEAT);
				}
			}
		}

		return new DefenseResult(attack.getDefendingArea(), attack.getAttackingArea(), rolledDice, lostAttackArmies, lostDefenseArmies, hasConqueredArea);
	}

	@Override
	public void moveArmiesAfterConquest(Player attacker, DefenseResult defense, int numberOfArmies) throws GameException {
		verifyTurn(attacker);

		if (attacker.getPhase() != Phase.ATTACK) {
			throw new GameException("Not players attack phase", GameExceptionType.WRONG_PHASE_FOR_ACTION);
		}

		if (!defense.hasConqueredArea()) {
			throw new GameException("Defending area was not conquered", GameExceptionType.NO_AREA_CONQUERED);
		}

		if (!attacker.equals(defense.getAttackingArea().getControllingPlayer()) || !attacker.equals(defense.getDefendingArea().getControllingPlayer())) {
			throw new GameException("Either attack area or defense area not under control by attacking player", GameExceptionType.AREA_NOT_UNDER_CONTROL);
		}

		if (defense.getAttackingArea().getArmies() - numberOfArmies < 1) {
			throw new GameException("Cannot move all armies out of field. One has to be left behind", GameExceptionType.ILLEGAL_NO_OF_ARMIES);
		}

		// assign new army count
		defense.getAttackingArea().setArmies(defense.getAttackingArea().getArmies() - numberOfArmies);
		defense.getDefendingArea().setArmies(defense.getDefendingArea().getArmies() + numberOfArmies);
	}

	@Override
	public Card drawCard(Player player) throws GameException {
		verifyTurn(player);

		if (player.getPhase() != Phase.DRAW_CARD) {
			throw new GameException("Not players draw card phase", GameExceptionType.WRONG_PHASE_FOR_ACTION);
		}

		if (!player.hasConqueredAreaThisTurn()) {
			throw new GameException("Player may not draw a card this turn", GameExceptionType.NO_AREA_CONQUERED);
		}

		if (this.availableCards == null || this.availableCards.isEmpty()) {
			throw new GameException("No more cards in stock - trading in?", GameExceptionType.UNKNOW_ERROR);
		}

		Random rd = new Random();
		int index = rd.nextInt(this.availableCards.size());

		Card card = this.availableCards.get(index);
		this.availableCards.remove(index);

		player.setPhase(Phase.FORTIFICATION);

		return card;
	}

	@Override
	public void fortify(Player player, Area source, Area destination, int numberOfArmies) throws GameException {
		verifyTurn(player);

		// validation stuff
		if (player.getPhase() != Phase.FORTIFICATION) {
			throw new GameException("Not players fortification phase", GameExceptionType.WRONG_PHASE_FOR_ACTION);
		}
		if (source == null || destination == null) {
			throw new GameException("Either source or destination are null", GameExceptionType.UNKNOW_ERROR);
		}
		if (!source.getControllingPlayer().equals(player) || !destination.getControllingPlayer().equals(player)) {
			throw new GameException("Either source or destination are not in players possesion", GameExceptionType.AREA_NOT_UNDER_CONTROL);
		}
		if (numberOfArmies > source.getArmies() - 1) {
			throw new GameException("The number of armies to be moved is higher than allowed.", GameExceptionType.ILLEGAL_NO_OF_ARMIES);
		}

		// verify that there is a valid path to the destination area
		List<Area> path = new ArrayList<>();

		// add source
		path.add(source);
		createPathList(player, source, path);

		if (path.contains(destination)) {
			source.setArmies(source.getArmies() - numberOfArmies);
			destination.setArmies(destination.getArmies() + numberOfArmies);
		} else {
			throw new GameException("The destination " + destination.toString() + " is not reachable from source " + source.toString(), GameExceptionType.ILLEGAL_PATH);
		}
	}

	@Override
	public void confirmEndOfPhase(Player player) throws GameException {
		verifyTurn(player);
		
		if (this.gameStatus == GameStatus.GAME_OVER) {
			throw new GameException("This game is already over", GameExceptionType.GAME_OVER);
		}

		switch (player.getPhase()) {
		case TRADE_CARDS:
		case TRADE_CARDS_AFTER_DEFEAT:
			if (player.getCards().size() > MAX_CARDS_IN_HAND_BEFORE_TRADE) {
				throw new GameException("Must trade cards before changing phase if more than 5 cards in hand!", GameExceptionType.MUST_TRADE_CARDS);
			}
			player.setPhase(Phase.REINFORCEMENT);
			if (player.getPhase() == Phase.TRADE_CARDS) {
				getAvailableReinforcements(player);			
			}
			break;
		case REINFORCEMENT:
			if (player.getNoOfReinforcements() > 0) {
				throw new GameException("Still " + player.getNoOfReinforcements() + " armies to set to board!", GameExceptionType.ILLEGAL_NO_OF_ARMIES);
			}
			player.setPhase(Phase.ATTACK);
			break;
		case ATTACK:
			if (player.hasConqueredAreaThisTurn()) {
				player.setPhase(Phase.DRAW_CARD);
			} else {
				player.setPhase(Phase.FORTIFICATION);
			}
			checkGoals();
			
			break;
		case FORTIFICATION:
			player.setConqueredAreaThisTurn(false);
			setNextPlayersTurn(true);
			break;
		default:
			throw new GameException("Invalid phase to confirm: " + player.getPhase(), GameExceptionType.WRONG_PHASE_FOR_ACTION);
		}
	}
	
	@Override
	public AreaList getAreas() {
		return this.allAreas;
	}
	
	@Override
	public GameStatus getGameStatus() {
		return this.gameStatus;
	}

	@Override
	public Player getWinner() {
		return this.winner;
	}

	/**
	 * fetches the deck with all remaining cards available for distribution
	 * 
	 * @return a deck of cards
	 */
	protected List<Card> getDeck() {
		return this.availableCards;
	}	

	private void verifyTurn(Player player) throws GameException {
		if (player == null) {
			throw new GameException("No player given - exiting", GameExceptionType.UNKNOW_ERROR);
		}
		if (!player.isTurn()) {
			throw new GameException("Not player '" + player.getName() + "s' turn", GameExceptionType.NOT_PLAYERS_TURN);
		}
	}

	private boolean verifyCardCombination(List<Card> cards) {
		boolean isCombinationOk = true;
		// card combination check - is each card of the same unit type
		for (Card c : cards) {
			for (Card checkCard : cards) {
				if (!checkCard.equals(c) && c.getUnitType() != checkCard.getUnitType() && !checkCard.getIsWildcard()) {
					isCombinationOk = false;
					break;
				}
			}
			if (!isCombinationOk) {
				break;
			}
		}

		if (!isCombinationOk) {
			isCombinationOk = true;
			// card combination check - is each card of a different unit type
			for (Card c : cards) {
				for (Card checkCard : cards) {
					if (!checkCard.equals(c) && c.getUnitType() == checkCard.getUnitType() && !checkCard.getIsWildcard()) {
						isCombinationOk = false;
						break;
					}
				}
				if (!isCombinationOk) {
					break;
				}
			}
		}

		return isCombinationOk;
	}

	/**
	 * creates a list of possible paths from the source area to areas in player
	 * possession
	 * 
	 * @param player
	 *            the player currently in action
	 * @param source
	 *            the source area where to get all paths from
	 * @param path
	 *            the current path (meaning all areas that can be reached
	 *            directly from the source)
	 */
	private void createPathList(Player player, Area source, List<Area> path) {
		// add all neighbors that are under player control
		for (Area neighbour : source.getNeighbours()) {
			// find that area in the allAreas list
			for (Area a : this.allAreas) {
				if (neighbour.equals(a) && a.getControllingPlayer().equals(player) && !path.contains(a)) {
					path.add(a);
					createPathList(player, a, path);
					break;
				} else if (neighbour.equals(a)) {
					break;
				}
			}
		}
	}

	/**
	 * set the next players turn
	 */
	private void setNextPlayersTurn(boolean switchPhase) {
		for (int i = 0; i < this.players.size(); i++) {
			if (this.players.get(i).isTurn()) {
				this.players.get(i).setTurn(false);
				if (switchPhase) {
					this.players.get(i).setPhase(Phase.NONE);
				}

				int index = 0;
				if (i < this.players.size() - 1) {
					index = i + 1;
				}
				this.players.get(index).setTurn(true);
				if (switchPhase) {
					this.players.get(index).setPhase(Phase.TRADE_CARDS);
				}

				break;
			}
		}
	}
	
	/**
	 * checks all players goals if they have been met and set the game status if so
	 */
	private void checkGoals() {
		for (Player player : this.players) {
			boolean hasWon = false;
			Continent[] continents;
			switch (player.getGoal()) {
			case CONQUER_NORTH_AMERICA_SOUTH_AMERICA_AND_AUSTRALIA:
				continents = new Continent[] {Continent.NORTH_AMERICA, Continent.SOUTH_AMERICA, Continent.AUSTRALIA};
				hasWon = hasMetCondition(player, Arrays.asList(continents), false);
				break;
			case CONQUER_AFRICA_SOUTH_AMERICA_AND_THIRD_CONTINENT:
				continents = new Continent[] {Continent.AFRICA, Continent.SOUTH_AMERICA};
				hasWon = hasMetCondition(player, Arrays.asList(continents), true);
				break;
			case CONQUER_24_AREAS:
				if (this.allAreas.getNumberOfAreasControlledByPlayer(player) >= CONTROLLED_AREAS_FOR_VICTORY) {
					hasWon = true;
				}
				break;
			case CONQUER_ASIA_AND_AFRICA:
				continents = new Continent[] {Continent.ASIA, Continent.AFRICA};
				hasWon = hasMetCondition(player, Arrays.asList(continents), false);
				break;
			case CONQUER_ASIA_AND_SOUTH_AMERICA:
				continents = new Continent[] {Continent.ASIA, Continent.SOUTH_AMERICA};
				hasWon = hasMetCondition(player, Arrays.asList(continents), false);
				break;
			case CONQUER_EUROPE_AUSTRALIA_AND_THIRD_CONTINENT:
				continents = new Continent[] {Continent.EUROPE, Continent.AUSTRALIA};
				hasWon = hasMetCondition(player, Arrays.asList(continents), true);
				break;
			case CONQUER_EUROPE_SOUTH_AMERICA_AND_THIRD_CONTINENT:
				continents = new Continent[] {Continent.EUROPE, Continent.SOUTH_AMERICA};
				hasWon = hasMetCondition(player, Arrays.asList(continents), true);
				break;
			case CONQUER_NORTH_AMERICA_AND_AFRICA:
				continents = new Continent[] {Continent.NORTH_AMERICA, Continent.AFRICA};
				hasWon = hasMetCondition(player, Arrays.asList(continents), false);
				break;
			case WORLD_DOMINATION:
			default:
				if (this.allAreas.isWorldDomination(player)) {
					hasWon = true;
				}
				break;
			}
			
			if (hasWon) {
				this.gameStatus = GameStatus.GAME_OVER;
				this.winner = player;
				break;
			}
		}
	}
	
	/**
	 * checks if the given player has met his goals by determining the state with the given parameters
	 * 
	 * @param player 
	 * 				the player that supposedly met his conditions
	 * @param continentsToConquer
	 * 				the mandatory continents to conquer for the player
	 * @param plusWildcardContinent
	 * 				if true the player has to conquer another continent - no matter which
	 * @return
	 * 			true, if the player met his conditions, false otherwise
	 */
	private boolean hasMetCondition(Player player, List<Continent> continentsToConquer, boolean plusWildcardContinent) {
		for (Continent continent : continentsToConquer) {
			// if one of the mandatory continents is not controlled by the player, return false
			if (!this.allAreas.controlsContinent(continent, player)) {
				return false;
			}
		}
		
		// check any continent not in the continent list if a wildcard is required
		if (plusWildcardContinent) {
			for (Continent continent : Continent.values()) {
				// don't check mandatories
				if (continentsToConquer.contains(continent)) {
					continue;
				}
				// if player controls a non-mandatory continent in addition, he has met his goals
				if (this.allAreas.controlsContinent(continent, player)) {
					return true;
				}
			}
		}
		
		return true;
	}
}
