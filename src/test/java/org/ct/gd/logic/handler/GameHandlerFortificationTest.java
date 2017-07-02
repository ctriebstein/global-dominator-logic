package org.ct.gd.logic.handler;

import junit.framework.TestCase;

import org.ct.gd.logic.exception.GameException;
import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.exception.GameException.GameExceptionType;
import org.ct.gd.logic.handler.GameHandler;
import org.ct.gd.logic.handler.GameHandlerImpl;
import org.ct.gd.logic.mapper.JsonMapper;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.Color;
import org.ct.gd.logic.model.Phase;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

public class GameHandlerFortificationTest extends TestCase {

	public void testNotPlayersTurn() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(false);

			gh.fortify(p1, areas.get(0), areas.get(1), 2);
			fail("This player is not allowed to fortify");
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

			gh.fortify(p1, areas.get(0), areas.get(1), 2);
			fail("This player is not allowed fortify");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.WRONG_PHASE_FOR_ACTION);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testAreasNotOk() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.FORTIFICATION);

			gh.fortify(p1, null, areas.get(1), 2);
			fail("An area is null");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.UNKNOW_ERROR);					
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testAreasNotPossesed() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.FORTIFICATION);
			
			areas.get(0).setControllingPlayer(p1);
			areas.get(1).setControllingPlayer(constructOponent());

			gh.fortify(p1, areas.get(0), areas.get(1), 2);
			fail("An area is not under player control");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.AREA_NOT_UNDER_CONTROL);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}

	public void testInvalidNumberOfArmies() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();

			p1.setTurn(true);
			p1.setPhase(Phase.FORTIFICATION);
			
			areas.get(0).setControllingPlayer(p1);
			areas.get(0).setArmies(2);
			areas.get(1).setControllingPlayer(p1);

			gh.fortify(p1, areas.get(0), areas.get(1), 2);
			fail("Player wants to move too many armies");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.ILLEGAL_NO_OF_ARMIES);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testIllegalPath() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();

			p1.setTurn(true);
			p1.setPhase(Phase.FORTIFICATION);
			
			Area source = null;
			Area destination = null;
			
			for (Area a : areas) {
				if (a.getName().equals("alaska")) {
					a.setControllingPlayer(p1);
					a.setArmies(5);
					source = a;
				} else if (a.getName().equals("ukraine")) {
					a.setControllingPlayer(p1);
					a.setArmies(1);
					destination = a;					
				} else {
					a.setControllingPlayer(p2);
				}
			}
			
			gh.fortify(p1, source, destination, 2);
			fail("The path is illegal");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.ILLEGAL_PATH);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testShortLegalPath() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();

			p1.setTurn(true);
			p1.setPhase(Phase.FORTIFICATION);
			
			Area source = null;
			Area destination = null;
			
			for (Area a : areas) {
				if (a.getName().equals("alaska")) {
					a.setControllingPlayer(p1);
					a.setArmies(5);
					source = a;
				} else if (a.getName().equals("kamtschatka")) {
					a.setControllingPlayer(p1);
					a.setArmies(1);
					destination = a;					
				} else {
					a.setControllingPlayer(p2);
				}
			}
			
			gh.fortify(p1, source, destination, 2);
			assertTrue(source.getArmies() == 3);
			assertTrue(destination.getArmies() == 3);
		} catch (GameException e) {
			fail("Everything is legal - this should not happen");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testLongLegalPathWithOnlyOneOption() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();

			p1.setTurn(true);
			p1.setPhase(Phase.FORTIFICATION);
			
			Area source = null;
			Area destination = null;
			
			for (Area a : areas) {
				if (a.getName().equals("alaska")) {
					a.setControllingPlayer(p1);
					a.setArmies(5);
					source = a;
				} else if (a.getName().equals("greenland")) {
					a.setControllingPlayer(p1);
					a.setArmies(1);
					destination = a;	
					
				} else if (a.getName().equals("alberta") || a.getName().equals("ontario")) {
					a.setControllingPlayer(p1);
					a.setArmies(1);									
				} else {
					a.setControllingPlayer(p2);
				}
			}
			
			gh.fortify(p1, source, destination, 2);
			assertTrue(source.getArmies() == 3);
			assertTrue(destination.getArmies() == 3);
		} catch (GameException e) {
			fail("Everything is legal - this should not happen");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testLongLegalPathWithMultipleOptions() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();			

			p1.setTurn(true);
			p1.setPhase(Phase.FORTIFICATION);
			
			Area source = null;
			Area destination = null;
			
			for (Area a : areas) {
				if (a.getName().equals("alaska")) {
					a.setControllingPlayer(p1);
					a.setArmies(5);
					source = a;
				} else if (a.getName().equals("ukraine")) {
					a.setControllingPlayer(p1);
					a.setArmies(1);
					destination = a;	
					
				} else {
					a.setControllingPlayer(p1);
				}
			}
			
			gh.fortify(p1, source, destination, 2);
			assertTrue(source.getArmies() == 3);
			assertTrue(destination.getArmies() == 3);
		} catch (GameException e) {
			fail("Everything is legal - this should not happen");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testCorrectPhase() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();

			p1.setTurn(true);
			p1.setPhase(Phase.FORTIFICATION);
			
			Area source = null;
			Area destination = null;
			
			for (Area a : areas) {
				if (a.getName().equals("alaska")) {
					a.setControllingPlayer(p1);
					a.setArmies(5);
					source = a;
				} else if (a.getName().equals("kamtschatka")) {
					a.setControllingPlayer(p1);
					a.setArmies(1);
					destination = a;					
				} else {
					a.setControllingPlayer(p2);
				}
			}
			
			gh.fortify(p1, source, destination, 2);
			assertTrue(source.getArmies() == 3);
			assertTrue(destination.getArmies() == 3);			
		} catch (GameException e) {
			fail("Everything is legal - this should not happen");
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
