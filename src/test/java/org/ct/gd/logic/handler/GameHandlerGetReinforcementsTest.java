package org.ct.gd.logic.handler;

import junit.framework.TestCase;

import org.ct.gd.logic.exception.GameException;
import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.exception.GameException.GameExceptionType;
import org.ct.gd.logic.handler.GameHandlerImpl;
import org.ct.gd.logic.mapper.JsonMapper;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.Color;
import org.ct.gd.logic.model.Phase;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

public class GameHandlerGetReinforcementsTest extends TestCase {

	public void testNotPlayersTurn() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandlerImpl gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(false);

			gh.getAvailableReinforcements(p1);
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

			GameHandlerImpl gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);

			gh.getAvailableReinforcements(p1);
			fail("This player is not allowed reinforce");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.WRONG_PHASE_FOR_ACTION);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testPlayerGetsMinimumArmiesOk() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandlerImpl gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.REINFORCEMENT);

			int armies = gh.getAvailableReinforcements(p1);
			assertTrue(armies == 3);			
		} catch (GameException e) {
			fail("There should be no exception here");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testPlayerGetsMaximumArmiesOk() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandlerImpl gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.REINFORCEMENT);
			
			for (Area a : areas) {
				a.setControllingPlayer(p1);
			}

			int armies = gh.getAvailableReinforcements(p1);
			// (all areas / 3) + europe + australia + asia + africa + south america + north america 
			// all continents = 23
			// all areas = 42 / 3 = 14
			assertTrue(armies == 37);			
		} catch (GameException e) {
			fail("There should be no exception here");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}

	private Player constructPlayer() {
		return new Player("Erika", Color.GREEN, false);
	}
}
