package org.ct.gd.logic.handler;

import junit.framework.TestCase;

import org.ct.gd.logic.exception.GameException;
import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.exception.GameException.GameExceptionType;
import org.ct.gd.logic.handler.GameHandler;
import org.ct.gd.logic.handler.GameHandlerImpl;
import org.ct.gd.logic.mapper.JsonMapper;
import org.ct.gd.logic.model.Color;
import org.ct.gd.logic.model.Phase;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

public class GameHandlerReinforceTest extends TestCase {

	public void testNotPlayersTurn() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(false);

			gh.reinforce(p1, areas.get(0), 5);
			fail("This player is not allowed to reinforce");
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
			p1.setPhase(Phase.ATTACK);

			gh.reinforce(p1, areas.get(0), 5);
			fail("This player is not allowed to reinforce");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.WRONG_PHASE_FOR_ACTION);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testNotPlayersArea() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.REINFORCEMENT);
			
			areas.get(0).setControllingPlayer(constructOponent());

			gh.reinforce(p1, areas.get(0), 5);
			fail("This player is not allowed to reinforce");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.AREA_NOT_UNDER_CONTROL);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testInvalidNoOfArmies() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.REINFORCEMENT);
			p1.setNoOfReinforcements(4);
			
			areas.get(0).setControllingPlayer(p1);

			gh.reinforce(p1, areas.get(0), 5);
			fail("The amount of armies is totally wrong");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.ILLEGAL_NO_OF_ARMIES);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testValidNoOfArmies() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.REINFORCEMENT);
			p1.setNoOfReinforcements(6);
			
			areas.get(0).setControllingPlayer(p1);

			gh.reinforce(p1, areas.get(0), 5);	
			assertTrue(p1.getNoOfReinforcements() == 1);
			assertTrue(areas.get(0).getArmies() == 5);
			
			gh.reinforce(p1, areas.get(0), 1);
			assertTrue(p1.getNoOfReinforcements() == 0);
			assertTrue(areas.get(0).getArmies() == 6);		
		} catch (GameException e) {
			fail("There should be no error here");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testPhaseChangeImpossible() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.REINFORCEMENT);
			p1.setNoOfReinforcements(6);
			
			areas.get(0).setControllingPlayer(p1);

			gh.reinforce(p1, areas.get(0), 5);	
			assertTrue(p1.getNoOfReinforcements() == 1);
			assertTrue(areas.get(0).getArmies() == 5);
			
			gh.confirmEndOfPhase(p1);
			fail("There should be an error here because of remaining reinforcements");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.ILLEGAL_NO_OF_ARMIES);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testPhaseChangePossible() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.REINFORCEMENT);
			p1.setNoOfReinforcements(6);
			
			areas.get(0).setControllingPlayer(p1);

			gh.reinforce(p1, areas.get(0), 6);	
			assertTrue(p1.getNoOfReinforcements() == 0);
			assertTrue(areas.get(0).getArmies() == 6);
			
			gh.confirmEndOfPhase(p1);		
			assertTrue(p1.getPhase() == Phase.ATTACK);
		} catch (GameException e) {
			fail("There should be no error here");
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
