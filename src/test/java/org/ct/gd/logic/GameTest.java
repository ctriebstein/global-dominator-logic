package org.ct.gd.logic;

import java.util.ArrayList;
import java.util.List;

import org.ct.gd.logic.Game;
import org.ct.gd.logic.exception.IllegalIdentityException;
import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.exception.InvalidNumberOfPlayersException;
import org.ct.gd.logic.handler.GameHandlerImpl;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.Color;
import org.ct.gd.logic.model.Goal;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

import junit.framework.TestCase;

public class GameTest extends TestCase {

	public void testInvalidNumberOfPlayers() {
		List<Player> players = new ArrayList<Player>();

		players.add(new Player("Erika", Color.GREEN, false));

		try {
			new Game(players, true);

			fail("The above command should have created an exception due to too few players!");
		} catch (InvalidNumberOfPlayersException e) {
			// this is an expected result
		} catch (IllegalIdentityException e) {
			fail("That is not supposed to happen in here!");
		} catch (InvalidMappingException e) {
			fail("That is not supposed to happen in here!");
		}

		players.add(new Player("Christian", Color.BLUE, false));
		players.add(new Player("Locke", Color.WHITE, false));
		players.add(new Player("Jack", Color.BLACK, false));
		players.add(new Player("Sawyer", Color.BROWN, false));
		players.add(new Player("Kate", Color.ORANGE, false));
		players.add(new Player("Hurley", Color.RED, false));
		players.add(new Player("Libby", Color.PINK, false));
		players.add(new Player("Michael", Color.YELLOW, false));

		try {
			new Game(players, true);

			fail("The above command should have created an exception due to too many players!");
		} catch (InvalidNumberOfPlayersException e) {
			// this is an expected result
		} catch (IllegalIdentityException e) {
			fail("That is not supposed to happen in here!");
		} catch (InvalidMappingException e) {
			fail("That is not supposed to happen in here!");
		}

		try {
			new Game(null, true);

			fail("The above command should have created an exception due to null players!");
		} catch (InvalidNumberOfPlayersException e) {
			// this is an expected result
		} catch (IllegalIdentityException e) {
			fail("That is not supposed to happen in here!");
		} catch (InvalidMappingException e) {
			fail("That is not supposed to happen in here!");
		}
	} 

	public void testValidNumberOfPlayers() {
		List<Player> players = new ArrayList<Player>();

		players.add(new Player("Erika", Color.GREEN, false));
		players.add(new Player("Christian", Color.BLUE, false));
		players.add(new Player("Locke", Color.WHITE, false));
		players.add(new Player("Jack", Color.BLACK, false));
		players.add(new Player("Sawyer", Color.BROWN, false));
		players.add(new Player("Kate", Color.ORANGE, false));
		players.add(new Player("Hurley", Color.RED, false));
		players.add(new Player("Libby", Color.PINK, false));

		try {
			new Game(players, true);
		} catch (InvalidNumberOfPlayersException e) {
			fail("The above command should not have created an exception because it's just 8 players!");
		} catch (IllegalIdentityException e) {
			fail("That is not supposed to happen in here!");
		} catch (InvalidMappingException e) {
			fail("That is not supposed to happen in here!");
		}
	}	

	public void testWorldDominationGoal() {
		List<Player> players = new ArrayList<Player>();

		players.add(new Player("Erika", Color.GREEN, false));
		players.add(new Player("Christian", Color.BLUE, false));
		players.add(new Player("Locke", Color.WHITE, false));
		players.add(new Player("Jack", Color.BLACK, false));
		players.add(new Player("Sawyer", Color.BROWN, false));
		players.add(new Player("Kate", Color.ORANGE, false));
		players.add(new Player("Hurley", Color.RED, false));
		players.add(new Player("Libby", Color.PINK, false));

		try {
			Game game = new Game(players, true);

			for (Player p : game.getPlayers()) {
				assertTrue(p.getGoal() == Goal.WORLD_DOMINATION);
			}
		} catch (InvalidNumberOfPlayersException e) {
			fail("The above command should not have created an exception because it's just 8 players!");
		} catch (IllegalIdentityException e) {
			fail("That is not supposed to happen in here!");
		} catch (InvalidMappingException e) {
			fail("That is not supposed to happen in here!");
		}
	}

	public void testNotWorldDominationGoal() {
		List<Player> players = new ArrayList<Player>();

		players.add(new Player("Erika", Color.GREEN, false));
		players.add(new Player("Christian", Color.BLUE, false));
		players.add(new Player("Locke", Color.WHITE, false));
		players.add(new Player("Jack", Color.BLACK, false));
		players.add(new Player("Sawyer", Color.BROWN, false));
		players.add(new Player("Kate", Color.ORANGE, false));
		players.add(new Player("Hurley", Color.RED, false));
		players.add(new Player("Libby", Color.PINK, false));

		try {
			Game game = new Game(players, false);

			for (Player p : game.getPlayers()) {
				assertTrue(p.getGoal() != Goal.WORLD_DOMINATION);

				for (Player checkPlayer : game.getPlayers()) {
					if (!p.equals(checkPlayer)) {
						assertFalse(p.getGoal() == checkPlayer.getGoal());
					}
				}
			}
		} catch (InvalidNumberOfPlayersException e) {
			fail("The above command should not have created an exception because it's just 8 players!");
		} catch (IllegalIdentityException e) {
			fail("That is not supposed to happen in here!");
		} catch (InvalidMappingException e) {
			fail("That is not supposed to happen in here!");
		}
	}

	public void testHasIllegalIdentities() {
		List<Player> players = new ArrayList<Player>();

		players.add(new Player("Erika", Color.GREEN, false));
		players.add(new Player("Erika", Color.BLUE, false));

		try {
			new Game(players, false);

			fail("Both players have the same name - this shouldn't be allowed!");
		} catch (InvalidNumberOfPlayersException e) {
			fail("The above command should not have created an exception because it's just 2 players!");
		} catch (IllegalIdentityException e) {
			// expected result
		} catch (InvalidMappingException e) {
			fail("That is not supposed to happen in here!");
		}

		players = new ArrayList<Player>();

		players.add(new Player("Erika", Color.GREEN, false));
		players.add(new Player("eRiKa", Color.BLUE, false));

		try {
			new Game(players, false);

			fail("Both players have the same name - this shouldn't be allowed!");
		} catch (InvalidNumberOfPlayersException e) {
			fail("The above command should not have created an exception because it's just 2 players!");
		} catch (IllegalIdentityException e) {
			// expected result
		} catch (InvalidMappingException e) {
			fail("That is not supposed to happen in here!");
		}
	}

	public void testHasIllegalColors() {
		List<Player> players = new ArrayList<Player>();

		players.add(new Player("Erika", Color.GREEN, false));
		players.add(new Player("Christian", Color.GREEN, false));

		try {
			new Game(players, false);

			fail("Both players have the same color - this shouldn't be allowed!");
		} catch (InvalidNumberOfPlayersException e) {
			fail("The above command should not have created an exception because it's just 2 players!");
		} catch (IllegalIdentityException e) {
			// expected result
		} catch (InvalidMappingException e) {
			fail("That is not supposed to happen in here!");
		}
	}
	
	public void testAreaAssignmentWorking() {
		List<Player> players = new ArrayList<Player>();

		players.add(new Player("Erika", Color.GREEN, false));
		players.add(new Player("Christian", Color.BLUE, false));
		players.add(new Player("Locke", Color.WHITE, false));
		players.add(new Player("Jack", Color.BLACK, false));
		players.add(new Player("Sawyer", Color.BROWN, false));
		players.add(new Player("Kate", Color.ORANGE, false));
		players.add(new Player("Hurley", Color.RED, false));
		players.add(new Player("Libby", Color.PINK, false));

		try {
			Game game = new Game(players, true);
			game.startGame();
			AreaList areas = ((GameHandlerImpl) game.getGameHandler()).getAreas();
			
			for (Area area : areas) {
				assertTrue(area.getArmies() == 1);
				assertTrue(area.getControllingPlayer() != null);
			}
		} catch (InvalidNumberOfPlayersException e) {
			fail("The above command should not have created an exception because it's just 8 players!");
		} catch (IllegalIdentityException e) {
			fail("That is not supposed to happen in here!");
		} catch (InvalidMappingException e) {
			fail("That is not supposed to happen in here!");
		}
	}
}
