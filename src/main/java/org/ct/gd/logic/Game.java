package org.ct.gd.logic;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.ct.gd.logic.exception.IllegalIdentityException;
import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.exception.InvalidNumberOfPlayersException;
import org.ct.gd.logic.handler.GameHandler;
import org.ct.gd.logic.handler.GameHandlerImpl;
import org.ct.gd.logic.mapper.JsonMapper;
import org.ct.gd.logic.model.Goal;
import org.ct.gd.logic.model.Phase;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * basic information about the game itself (if started)
 * 
 * @author ct
 * 
 */
public class Game implements Serializable {

	private static final long serialVersionUID = 8028933546820328347L;

	private static final int MINIMUM_PLAYERS = 2;
	private static final int MAXIMUM_PLAYERS = 8;

	/**
	 * Set initial army values for distribution
	 */
	private static final int INITIAL_ARMIES_FOR_MINIMUM_PLAYERS = 40;
	/**
	 * for each additional player more than two decrease the initial army value
	 * by this constant
	 */
	private static final int INITIAL_DECREASE_PER_PLAYER = 5;

	private String id;
	
	private int turn;
	private List<Player> players;

	/**
	 * Map game prerequisites
	 */
	private GameHandler gameHandler;

	/**
	 * determines whether or not the goal of the game is global domination
	 */
	private boolean isGlobalDomination;

	/**
	 * basic constructor creating a game with all players
	 * 
	 * @param players
	 *            all players in this game
	 * @param isGlobalDomination
	 *            true, if the goal of the game is global domination
	 *            (controlling all areas)
	 */
	public Game(List<Player> players, boolean isGlobalDomination) throws InvalidNumberOfPlayersException, IllegalIdentityException, InvalidMappingException {

		if (players == null || players.size() < MINIMUM_PLAYERS || players.size() > MAXIMUM_PLAYERS) {
			throw new InvalidNumberOfPlayersException("The number of players has to be between " + MINIMUM_PLAYERS + " and " + MAXIMUM_PLAYERS);
		}
		if (hasIllegalIdentities(players)) {
			throw new IllegalIdentityException("Players may not have the same name and/or color!");
		}
		
		this.id = UUID.randomUUID().toString();

		this.players = players;
		this.isGlobalDomination = isGlobalDomination;
		this.setTurn(1);

		// assign initial armies to each player
		int numberOfInitialArmies = INITIAL_ARMIES_FOR_MINIMUM_PLAYERS - ((this.players.size() - MINIMUM_PLAYERS) * INITIAL_DECREASE_PER_PLAYER);

		for (Player p : this.players) {
			p.setNoOfReinforcements(numberOfInitialArmies);
			p.setPhase(Phase.INITIAL_PLACEMENT);
		}

		if (!isGlobalDomination) {
			assignRandomGoals();
		}
	}
	
	public String getId() {
		return this.id;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public boolean isGlobalDomination() {
		return this.isGlobalDomination;
	}	

	/**
	 * @return the basic gamehandler containing all possible actions for each
	 *         player
	 */
	@JsonIgnore
	public GameHandler getGameHandler() {
		return this.gameHandler;
	}

	/**
	 * public method actually starting the game in its current configuration
	 */
	public void startGame() throws InvalidMappingException {
		// pick random player who starts placing units
		Random rd = new Random();
		int startingPlayerIndex = rd.nextInt(this.players.size());
		this.players.get(startingPlayerIndex).setTurn(true);

		// assign areas to players
		assignAreas();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Game)) {
			return false;
		}
		
		Game g = (Game) obj;
		return g.getId().equals(this.getId());
	}
	
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}

	/**
	 * at the start of the game all areas available need to be assigned to one
	 * player
	 */
	private void assignAreas() throws InvalidMappingException {
		JsonMapper mapper = new JsonMapper();
		AreaList areas = mapper.mapAreasFromJson();
		AreaList assignedAreas = new AreaList();

		Random rd = new Random();
		while (!areas.isEmpty()) {
			for (Player player : this.players) {
				if (!areas.isEmpty()) {
					int areaIndex = rd.nextInt(areas.size());

					areas.get(areaIndex).setControllingPlayer(player);
					areas.get(areaIndex).setArmies(1);
					player.setNoOfReinforcements(player.getNoOfReinforcements() - 1);

					assignedAreas.add(areas.get(areaIndex));
					areas.remove(areaIndex);
				} else {
					break;
				}
			}
		}

		this.gameHandler = new GameHandlerImpl(mapper.mapCardsFromJson(), assignedAreas, this.players);
	}

	/**
	 * if the goal of the game is not world domination, each player is assigned
	 * a random (unique) goal at game initialization
	 */
	private void assignRandomGoals() throws InvalidNumberOfPlayersException {
		// make sure there are enough players
		if (this.players == null || this.players.size() < MINIMUM_PLAYERS || this.players.size() > MAXIMUM_PLAYERS) {
			throw new InvalidNumberOfPlayersException("The number of players has to be between " + MINIMUM_PLAYERS + " and " + MAXIMUM_PLAYERS);
		}

		Random random = new Random();

		int[] goals = new int[this.players.size()];

		// determine & assign goals
		for (int i = 0; i < goals.length; i++) {
			int goal = random.nextInt(Goal.getNumberOfGoals() - 1) + 1;

			// check if that number already exists - if so assign new one
			while (goalAlreadyExists(goal, goals)) {
				goal = random.nextInt(Goal.getNumberOfGoals() - 1) + 1;
			}

			goals[i] = goal;
			this.players.get(i).setGoal(Goal.getGoalByGoalNumber(goals[i]));
		}
	}

	/**
	 * checks whether or not a given goal number is already in a specified array
	 * 
	 * @param goalNumber
	 *            the goal number to assign
	 * @param goals
	 *            the goal numbers already assigned
	 * @return true, if the goalNumber is already part of goals, false otherwise
	 */
	private boolean goalAlreadyExists(int goalNumber, int[] goals) {
		if (goals == null || goals.length == 0) {
			return false;
		}

		for (int i = 0; i < goals.length; i++) {
			if (goals[i] != 0 && goals[i] == goalNumber) {
				return true;
			}
		}

		return false;
	}

	/**
	 * checks if players stole other players identities (like name or color)
	 * 
	 * @param players
	 *            the defined list of players
	 * @return true, if at least to players share the same name and/or color,
	 *         false otherwise
	 */
	private boolean hasIllegalIdentities(List<Player> players) {
		if (players == null || players.isEmpty()) {
			return true;
		}

		for (Player p : players) {
			for (Player checkPlayer : players) {
				if (!p.equals(checkPlayer) && (p.getName().equalsIgnoreCase(checkPlayer.getName()) || p.getColor() == checkPlayer.getColor())) {
					return true;
				}
			}
		}

		return false;
	}
}
