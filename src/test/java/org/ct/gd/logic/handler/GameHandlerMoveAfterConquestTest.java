package org.ct.gd.logic.handler;

import junit.framework.TestCase;

import org.ct.gd.logic.exception.GameException;
import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.exception.GameException.GameExceptionType;
import org.ct.gd.logic.handler.GameHandler;
import org.ct.gd.logic.handler.GameHandlerImpl;
import org.ct.gd.logic.mapper.JsonMapper;
import org.ct.gd.logic.model.Color;
import org.ct.gd.logic.model.DefenseResult;
import org.ct.gd.logic.model.Phase;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

public class GameHandlerMoveAfterConquestTest extends TestCase {

	public void testNotPlayersTurn() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(false);
			
			DefenseResult dr = new DefenseResult(areas.get(0), areas.get(1), new Integer[] {1, 1}, 0, 1, true);
			gh.moveArmiesAfterConquest(p1, dr, 2);
			fail("This player is not allowed to move");
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

			DefenseResult dr = new DefenseResult(areas.get(0), areas.get(1), new Integer[] {1, 1}, 0, 1, true);
			gh.moveArmiesAfterConquest(p1, dr, 2);
			fail("This player is not allowed move");
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
			
			areas.get(0).setControllingPlayer(p1);
			areas.get(1).setControllingPlayer(p2);

			DefenseResult dr = new DefenseResult(areas.get(0), areas.get(1), new Integer[] {1, 1}, 0, 1, true);
			gh.moveArmiesAfterConquest(p1, dr, 2);
			fail("This player doesn't control the attacking area");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.AREA_NOT_UNDER_CONTROL);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testHasNotConqueredArea() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);			
			Player p2 = constructOponent();

			p2.setTurn(true);
			p2.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p2);
			areas.get(1).setControllingPlayer(p2);

			DefenseResult dr = new DefenseResult(areas.get(0), areas.get(1), new Integer[] {1, 1}, 0, 1, false);
			gh.moveArmiesAfterConquest(p2, dr, 2);
			fail("This player didn't conquer anything - jerk");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.NO_AREA_CONQUERED);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testInvalidNoOfArmies() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);			
			Player p2 = constructOponent();

			p2.setTurn(true);
			p2.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p2);
			areas.get(0).setArmies(1);
			areas.get(1).setControllingPlayer(p2);
			areas.get(1).setArmies(2);

			DefenseResult dr = new DefenseResult(areas.get(0), areas.get(1), new Integer[] {1, 1}, 0, 1, true);
			gh.moveArmiesAfterConquest(p2, dr, 2);
			fail("One army needs to be left behind at least");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.ILLEGAL_NO_OF_ARMIES);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testSuccessfulMove() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p2 = constructOponent();

			p2.setTurn(true);
			p2.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p2);
			areas.get(0).setArmies(1);
			areas.get(1).setControllingPlayer(p2);
			areas.get(1).setArmies(3);

			DefenseResult dr = new DefenseResult(areas.get(0), areas.get(1), new Integer[] {1, 1}, 0, 1, true);
			gh.moveArmiesAfterConquest(p2, dr, 2);
			
			assertTrue(areas.get(0).getArmies() == 3);
			assertTrue(areas.get(1).getArmies() == 1);
		} catch (GameException e) {
			fail("Move should be successful");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
		
	private Player constructPlayer() {
		return new Player("Erika", Color.GREEN, false);
	}

	private Player constructOponent() {
		return new Player("Christian", Color.BLUE, false);
	}
}
