package org.ct.gd.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.ct.gd.logic.exception.GameException;
import org.ct.gd.logic.exception.IllegalIdentityException;
import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.exception.InvalidNumberOfPlayersException;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.AttackResult;
import org.ct.gd.logic.model.Card;
import org.ct.gd.logic.model.Color;
import org.ct.gd.logic.model.DefenseResult;
import org.ct.gd.logic.model.Player;

/**
 * Game Console starter application
 * 
 */
public class Starter {

	private Game game;
	private List<Player> players;

	public Starter() {
		this.players = new ArrayList<>();
	}

	public static void main(String[] args) {
		Starter starter = new Starter();
		starter.startConsole();
	}

	public void startConsole() {
		Scanner in = new Scanner(System.in);
		System.out.println("Welcome to the global dominator gaming console. Please type one of the following commands to start a game:");
		System.out.println("\taddplayer <name> <color>");
		System.out.println("\tshowplayers");
		System.out.println("\tstartgame");
		System.out.println("\tquit");
		System.out.println("\r\nThe following colors are available: RED, GREEN, BLUE, YELLOW, BLACK, WHITE, PINK, ORANGE, BROWN");
		System.out.println("The game can only be started with at least 2 and maximum 8 players.");
		System.out.println("Have fun!");

		while (true) {
			System.out.print("> ");
			String command = in.nextLine();

			if (command.equalsIgnoreCase("quit")) {
				break;
			}

			try {
				parseInitialCommand(command);

				if (this.game != null) {
					break;
				}
			} catch (IOException e) {
				System.err.println("The command you entered was not recognized!");
			}
		}

		System.out.println("The game has been started!");
		System.out.println("The following area assignments have been made:");
		printAreaInformation();

		System.out.println("To get a command overview type 'help'!");

		while (true) {
			System.out.print(this.game.getGameHandler().getPlayerInTurn().getName() + " (" + this.game.getGameHandler().getPlayerInTurn().getPhase() + ") > ");
			String command = in.nextLine();

			if (command.equalsIgnoreCase("quit")) {
				System.out.println("Ok - bye!");
				break;
			}

			try {
				parseCommand(command, in);
			} catch (GameException e) {
				System.err.println("There was a game error - please change your command. Error details:\r\n\t" + e.getMessage());
			} catch (IOException e) {
				System.err.println("The command you entered was not recognized!");
			}
		}

		in.close();
	}

	private void printCommandHelp() {
		System.out.println("The following commands can be entered now:");
		System.out.println("\thelp:\t\t\t Display this help screen");
		System.out.println("\tquit:\t\t\t End the game");
		System.out.println("\tareaInfo:\t\t\t Display the current area state of each area on the board");
		System.out.println("\tphase:\t\t\t Display the current phase of the turn");
		System.out.println("\tcards:\t\t\t Display all cards currently in players hands");
		System.out.println("\tplaceUnit <area>:\t\t\t Place a single unit to a controlled area (in phase INITIAL_PLACEMENT)");
		System.out.println("\ttradeCards <card1> <card2> <card3>:\t Trading cards (in phase TRADE_CARDS)");
		System.out.println("\treinforcements:\t\t\t Display all reinforcements available for the current player");
		System.out.println("\treinforce <area> <armies>:\t Places <armies> units to a controlled area (in phase REINFORCE)");
		System.out.println("\tattack <from> <to> <dice>: Attacks an area from a certain area with a given number of dice (in phase ATTACK)");		
		System.out.println("\tendphase:\t\t\t Ends the current phase");
	}

	private void printAreaInformation() {
		for (Area area : this.game.getGameHandler().getAreas()) {
			System.out.println(area.toString() + " controlled by " + area.getControllingPlayer().toString() + " with an unit count of " + area.getArmies());
		}
	}

	private void parseInitialCommand(String command) throws IOException {

		if (command.startsWith("addplayer")) {
			String[] commandSplitted = command.split(" ");

			if (commandSplitted.length != 3) {
				throw new IOException();
			}

			Color color = null;
			try {
				color = Color.valueOf(commandSplitted[2].toUpperCase());
			} catch (IllegalArgumentException e) {
				System.err.println("The given color doesn't exist!");
				return;
			}

			Player player = new Player(commandSplitted[1], color, false);

			for (Player p : this.players) {
				if (p.getName().equalsIgnoreCase(player.getName())) {
					System.err.println("This player name already exists!");
					return;
				} else if (p.getColor() == player.getColor()) {
					System.err.println("This color is already taken!");
					return;
				}
			}

			this.players.add(player);

		} else if (command.equalsIgnoreCase("showplayers")) {
			for (Player p : this.players) {
				System.out.println(p.toString());
			}
		} else if (command.equalsIgnoreCase("startgame")) {
			try {
				this.game = new Game(this.players, true);
				this.game.startGame();
			} catch (InvalidMappingException e) {
				System.err.println(e.getMessage());
			} catch (IllegalIdentityException e) {
				System.err.println(e.getMessage());
			} catch (InvalidNumberOfPlayersException e) {
				System.err.println(e.getMessage());
			}

		} else {
			throw new IOException();
		}
	}

	private void parseCommand(String command, Scanner in) throws IOException, GameException {
		if (command.equalsIgnoreCase("help")) {
			this.printCommandHelp();
		} else if (command.equalsIgnoreCase("areaInfo")) {
			this.printAreaInformation();
		} else if (command.equalsIgnoreCase("phase")) {
			System.out.println(this.game.getGameHandler().getPlayerInTurn().getPhase());
		} else if (command.equalsIgnoreCase("cards")) {
			this.printCardInformation();
		} else if (command.toLowerCase().startsWith("placeunit")) {
			this.placeUnit(command);
		} else if (command.toLowerCase().startsWith("tradeCards")) {
			this.tradeCards(command);
		} else if (command.equalsIgnoreCase("reinforcements")) {
			System.out.println(this.game.getGameHandler().getPlayerInTurn().getNoOfReinforcements());
		} else if (command.toLowerCase().startsWith("reinforce")) {
			this.reinforce(command);
		} else if (command.toLowerCase().startsWith("attack")) {
			this.attack(command, in);
		} else if (command.equalsIgnoreCase("endphase")) {
			this.game.getGameHandler().confirmEndOfPhase(this.game.getGameHandler().getPlayerInTurn());
		} else {
			throw new IOException();
		}
	}

	private void printCardInformation() {
		List<Card> cards = this.game.getGameHandler().getPlayerInTurn().getCards();

		for (Card card : cards) {
			System.out.println("\tCard: " + card.toString());
		}
	}

	private void placeUnit(String command) throws GameException {
		String[] splitted = command.split(" ");

		if (splitted.length != 2) {
			System.err.println("The command was not correct - please enter only the area label to place a unit to");
		}

		boolean found = false;
		for (Area area : this.game.getGameHandler().getAreas()) {
			if (area.getName().equalsIgnoreCase(splitted[1])) {
				found = true;
				System.out.println("Still " + (this.game.getGameHandler().getPlayerInTurn().getNoOfReinforcements() - 1) + " units to place!");
				this.game.getGameHandler().placeInitialUnit(this.game.getGameHandler().getPlayerInTurn(), area);
				System.out.println("Unit placed");
				break;
			}
		}

		if (!found) {
			System.err.println("The given area was not found - try again");
		}
	}

	private void tradeCards(String command) throws GameException {
		String[] splitted = command.split(" ");

		if (splitted.length != 4) {
			System.err.println("The command was not correct - please trade only 3 cards at a time");
		}

		List<Card> cardsToTrade = new ArrayList<Card>();
		for (Card card : this.game.getGameHandler().getPlayerInTurn().getCards()) {
			for (int i = 1; i < splitted.length; i++) {
				if (card.getArea().getName().equals(splitted[i]) && !cardsToTrade.contains(card)) {
					cardsToTrade.add(card);
				}
			}
		}
		List<List<Card>> tradeList = new ArrayList<>();
		tradeList.add(cardsToTrade);

		int armies = this.game.getGameHandler().tradeCards(this.game.getGameHandler().getPlayerInTurn(), tradeList);

		System.out.println("You're getting " + armies + " units to distribute");
	}

	private void reinforce(String command) throws GameException {
		String[] splitted = command.split(" ");

		if (splitted.length != 3) {
			System.err.println("The command was not correct - please supply the area and the number of armies to place");
		}

		int armies = 0;

		try {
			armies = Integer.parseInt(splitted[2]);
		} catch (Exception e) {
			System.err.println("<armies> needs to be a numerical value");
			return;
		}

		boolean found = false;
		for (Area area : this.game.getGameHandler().getAreas()) {
			if (area.getName().equalsIgnoreCase(splitted[1])) {
				found = true;				
				this.game.getGameHandler().reinforce(this.game.getGameHandler().getPlayerInTurn(), area, armies);
				System.out.println("Still " + this.game.getGameHandler().getPlayerInTurn().getNoOfReinforcements() + " units to place!");
				break;
			}
		}

		if (!found) {
			System.err.println("The given country was not found - try again");
		}
	}
	
	private void attack(String command, Scanner in) throws GameException {
		String[] splitted = command.split(" ");

		if (splitted.length != 4) {
			System.err.println("The command was not correct - please supply the area and the number of armies to place");
		}
		
		int dice = 0;

		try {
			dice = Integer.parseInt(splitted[3]);
		} catch (Exception e) {
			System.err.println("<dice> needs to be a numerical value between 1 and 3");
			return;
		}
		
		Area attack = null;
		Area defend = null;
		for (Area area : this.game.getGameHandler().getAreas()) {
			if (area.getName().equalsIgnoreCase(splitted[1])) {
				attack = area;
			} else if (area.getName().equalsIgnoreCase(splitted[2])) {
				defend = area;
			}
		}
		
		if (attack == null) {
			System.err.println("The given attack area was not found - try again");
			return;
		}
		if (defend == null) {
			System.err.println("The given defend area was not found - try again");
			return;
		}
		
		AttackResult ar = this.game.getGameHandler().attack(this.game.getGameHandler().getPlayerInTurn(), attack, defend, dice);
		
		System.out.println("The result of the attack:");
		for (int i = 0;i < ar.getAttackValues().length;i++) {
			System.out.println("\tAttack: " + ar.getAttackValues()[i]);
		}				
				
		DefenseResult dr = null;
		while (dr == null) {
			System.out.print("\r\n" + ar.getDefendingArea().getControllingPlayer().getName() + ", how many dice should be used for defense? > ");			
			String defenseDice = in.nextLine();			

			try {
				int dd = Integer.parseInt(defenseDice);
				dr = this.game.getGameHandler().defend(ar.getDefendingArea().getControllingPlayer(), ar, dd);
			} catch (GameException e) {
				System.err.println("There was a game error - please change your command. Error details:\r\n\t" + e.getMessage());
			} catch (Exception e) {
				System.err.println("Invalid number given");
			}
		}		
		
		System.out.println("The result of the defense:");
		for (int i = 0;i < dr.getDefenseValues().length;i++) {
			System.out.println("\tDefense: " + dr.getDefenseValues()[i]);
		}	
		
		
		if (dr.hasConqueredArea()) {
			System.out.println("\r\n" + this.game.getGameHandler().getPlayerInTurn().getName() + ", you conquered that area.");
					
			int moveInCount = -1;
			while (moveInCount < 0) {
				System.out.print("How many armies should move in (You have " + (ar.getAttackingArea().getArmies() - 1) + " armies to move) > ");			
				String moveInString = in.nextLine();			

				try {
					moveInCount = Integer.parseInt(moveInString);
					this.game.getGameHandler().moveArmiesAfterConquest(this.game.getGameHandler().getPlayerInTurn(), dr, moveInCount);
				} catch (GameException e) {
					System.err.println("There was a game error - please change your command. Error details:\r\n\t" + e.getMessage());
				} catch (Exception e) {
					System.err.println("Invalid number given");
				}
			}	
		}				
	}
}
