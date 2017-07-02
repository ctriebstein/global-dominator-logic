package org.ct.gd.logic.handler;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ct.gd.logic.exception.GameException;
import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.exception.GameException.GameExceptionType;
import org.ct.gd.logic.handler.GameHandler;
import org.ct.gd.logic.handler.GameHandlerImpl;
import org.ct.gd.logic.mapper.JsonMapper;
import org.ct.gd.logic.model.AttackResult;
import org.ct.gd.logic.model.Color;
import org.ct.gd.logic.model.DefenseResult;
import org.ct.gd.logic.model.Phase;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

public class GameHandlerDefenseTest extends TestCase {

	public void testDefenseAreaNotUnderControl() {
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
			AttackResult ar = new AttackResult(areas.get(1), areas.get(0), new Integer[] {1, 1});

			gh.defend(p1, ar, 2);
			fail("This player doesn't control the defending area");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.AREA_NOT_UNDER_CONTROL);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testInvalidDiceNumber() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p2);
			areas.get(1).setControllingPlayer(p1);
			AttackResult ar = new AttackResult(areas.get(1), areas.get(0), new Integer[] {1, 1});

			gh.defend(p2, ar, 3);
			fail("This player doesn't control the defending area");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.INVALID_DEFENSE_DICE);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testInvalidDiceNumberForArmiesInField() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p2);
			areas.get(0).setArmies(1);
			areas.get(1).setControllingPlayer(p1);			
			AttackResult ar = new AttackResult(areas.get(1), areas.get(0), new Integer[] {1, 1});

			gh.defend(p2, ar, 2);
			fail("This player doesn't control the defending area");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.INVALID_DEFENSE_DICE);
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testProperDefenseWin() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			GameHandler gh = new GameHandlerImpl(null, areas, null);
			Player p1 = constructPlayer();
			Player p2 = constructOponent();

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p2);
			areas.get(0).setArmies(2);
			areas.get(1).setControllingPlayer(p1);
			areas.get(1).setArmies(4);
			AttackResult ar = new AttackResult(areas.get(1), areas.get(0), new Integer[] {1, 1, 1});

			DefenseResult dr = gh.defend(p2, ar, 2);
			assertTrue(dr.getLostAttackingArmies() == 2);
			assertTrue(dr.getLostDefendingArmies() == 0);
			assertTrue(dr.hasConqueredArea() == false);
			assertTrue(dr.getAttackingArea().getArmies() == 2);
			assertTrue(dr.getDefendingArea().getArmies() == 2);
		} catch (GameException e) {
			fail("There should be no exception here");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	public void testProperDefenseLoss() {
		try {
			JsonMapper mapper = new JsonMapper();
			AreaList areas = mapper.mapAreasFromJson();

			Player p1 = constructPlayer();
			Player p2 = constructOponent();
			List<Player> players = new ArrayList<>();
			players.add(p1);
			players.add(p2);
			GameHandler gh = new GameHandlerImpl(null, areas, players);
			

			p1.setTurn(true);
			p1.setPhase(Phase.ATTACK);
			
			areas.get(0).setControllingPlayer(p2);
			areas.get(0).setArmies(2);
			areas.get(1).setControllingPlayer(p1);
			areas.get(1).setArmies(4);
			AttackResult ar = new AttackResult(areas.get(1), areas.get(0), new Integer[] {6, 6, 6});

			DefenseResult dr = gh.defend(p2, ar, 2);
			// attack with same values as long as this defending area is under p2s control
			int previousAttackArmies = 4;
			int previousDefenseArmies = 2;
			while(dr.hasConqueredArea() == false) {							
				assertTrue(dr.getAttackingArea().getArmies() == (previousAttackArmies - dr.getLostAttackingArmies()));
				assertTrue(dr.getDefendingArea().getArmies() == (previousDefenseArmies - dr.getLostDefendingArmies()));
				
				previousAttackArmies = dr.getAttackingArea().getArmies();
				previousDefenseArmies = dr.getDefendingArea().getArmies();
				
				dr.getAttackingArea().setArmies(4);
				ar = new AttackResult(dr.getAttackingArea(), dr.getDefendingArea(), new Integer[] {6, 6, 6});
				dr = gh.defend(p2, ar, 1);									
			}
						
			assertTrue(dr.getLostDefendingArmies() >= 1);
			assertTrue(dr.hasConqueredArea());
			assertTrue(dr.getDefendingArea().getControllingPlayer().equals(p1));
			assertTrue(dr.getDefendingArea().getArmies() >= 1);
						
		} catch (GameException e) {
			fail("There should be no exception here");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		}
	}
	
	// TODO: Test passing cards if player is eliminated
		
	private Player constructPlayer() {
		return new Player("Erika", Color.GREEN, false);
	}

	private Player constructOponent() {
		return new Player("Christian", Color.BLUE, false);
	}
}
