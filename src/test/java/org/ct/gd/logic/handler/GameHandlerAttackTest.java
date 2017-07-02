package org.ct.gd.logic.handler;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ct.gd.logic.Game;
import org.ct.gd.logic.exception.GameException;
import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.exception.GameException.GameExceptionType;
import org.ct.gd.logic.handler.GameHandler;
import org.ct.gd.logic.handler.GameHandlerImpl;
import org.ct.gd.logic.mapper.JsonMapper;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.AttackResult;
import org.ct.gd.logic.model.Color;
import org.ct.gd.logic.model.DefenseResult;
import org.ct.gd.logic.model.Phase;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

public class GameHandlerAttackTest extends TestCase {

	public void testNotPlayersTurn() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(false);

			gh.attack(p1, areas.get(0), areas.get(1), 2);
			fail("This player is not allowed to attack");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.NOT_PLAYERS_TURN);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}

	public void testNotPlayersPhase() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.FORTIFICATION);

			gh.attack(p1, areas.get(0), areas.get(1), 2);
			fail("This player is not allowed attack");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.WRONG_PHASE_FOR_ACTION);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testAttackAreaNotUnderControl() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p2);
			areas.get(1).setControllingPlayer(p2);

			gh.attack(p1, areas.get(0), areas.get(1), 2);
			fail("This player doesn't control the attacking area");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.AREA_NOT_UNDER_CONTROL);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testDefendingAreaUnderControl() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();			

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p1);
			areas.get(1).setControllingPlayer(p1);

			gh.attack(p1, areas.get(0), areas.get(1), 2);
			fail("This player does control the defending area");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.AREA_NOT_ATTACKABLE);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testAreasNotNeighbours() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();		

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p1);
			areas.get(20).setControllingPlayer(p2);

			gh.attack(p1, areas.get(0), areas.get(20), 2);
			fail("This player does control the defending area");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.AREA_NOT_ATTACKABLE);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testInvalidNoOfDice() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p1);
			areas.get(1).setControllingPlayer(p2);

			gh.attack(p1, areas.get(0), areas.get(1), 5);
			fail("This player doesn't control the attacking area");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.INVALID_ATTACK_DICE);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testInvalidNoOfDiceForArmies() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p1);
			areas.get(0).setArmies(3);
			areas.get(1).setControllingPlayer(p2);

			gh.attack(p1, areas.get(0), areas.get(1), 3);
			fail("This player doesn't control the attacking area");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.INVALID_ATTACK_DICE);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testValidAttack() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p1);
			areas.get(0).setArmies(5);
			areas.get(1).setControllingPlayer(p2);

			AttackResult ar = gh.attack(p1, areas.get(0), areas.get(1), 3);
			assertTrue(ar.getAttackingArea().equals(areas.get(0)));
			assertTrue(ar.getDefendingArea().equals(areas.get(1)));
			assertTrue(ar.getAttackValues().length == 3);
			
			// if there is an error in the code this test might fail sometimes as the numbers assigned for the dice are determined on random
			for (int i = 0;i < ar.getAttackValues().length;i++) {
				assertTrue(ar.getAttackValues()[i] <= 6 && ar.getAttackValues()[i] >= 1);	
			}
		} catch (GameException e) {
			fail("The attack should definitely work");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testDefeatedPlayerIsRemoved() {
		try {
			List<Player> players = new ArrayList<>();
			Player p1 = constructPlayer();
			Player p2 = constructOponent();
			players.add(p1);
			players.add(p2);
			
			Game game = new Game(players, true);
			game.startGame();

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);
			
			Area attacker = null;
			String defenderName = null;
			for (Area area : game.getGameHandler().getAreas()) {
				area.setControllingPlayer(p1);
				area.setArmies(5000);
				defenderName = area.getNeighbours().get(0).getName();
				attacker = area;
			}
			
			Area defender = game.getGameHandler().getAreas().getByName(defenderName);
			defender.setControllingPlayer(p2);
			defender.setArmies(1);

			DefenseResult dr;
			do {
				AttackResult ar = game.getGameHandler().attack(p1, attacker, defender, 3);
				dr = game.getGameHandler().defend(p2, ar, 1);
			} while(!dr.hasConqueredArea());
			
			assertTrue(game.getPlayers().size() == 1);
			assertEquals("Erika", game.getPlayers().get(0).getName());
		} catch (GameException e) {
			fail("The attack should definitely work");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		} catch (Exception e) {
			fail("Game initialization shouldn't be a problem here");
		}
	}
		
	private Player constructPlayer() {
		return new Player("Erika", Color.GREEN, false);
	}

	private Player constructOponent() {
		return new Player("Christian", Color.BLUE, false);
	}
}
