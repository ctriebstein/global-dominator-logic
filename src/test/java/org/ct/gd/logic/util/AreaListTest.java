package org.ct.gd.logic.util;

import java.util.List;

import junit.framework.TestCase;

import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.mapper.JsonMapper;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.Color;
import org.ct.gd.logic.model.Continent;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

public class AreaListTest extends TestCase {

	public void testAreasInContinentCorrect() {
		JsonMapper mapper = new JsonMapper();

		try {			
			AreaList areas = mapper.mapAreasFromJson();

			assertTrue(areas != null && areas.size() > 0);
			
			assertEquals(areas.getAreasInContinent(Continent.AUSTRALIA).size(), 4);
			assertEquals(areas.getAreasInContinent(Continent.ASIA).size(), 12);
			assertEquals(areas.getAreasInContinent(Continent.EUROPE).size(), 7);
			assertEquals(areas.getAreasInContinent(Continent.AFRICA).size(), 6);
			assertEquals(areas.getAreasInContinent(Continent.NORTH_AMERICA).size(), 9);
			assertEquals(areas.getAreasInContinent(Continent.SOUTH_AMERICA).size(), 4);
			
		} catch (InvalidMappingException e) {
			// this shouldn't happen
			fail("the mapping should work!\r\n" + e.getMessage());
		}
	}
	
	public void testControlsContinentTrue() {
		JsonMapper mapper = new JsonMapper();

		try {			
			AreaList areas = mapper.mapAreasFromJson();

			assertTrue(areas != null && areas.size() > 0);

			Player player1 = new Player("Erika", Color.GREEN, false);
			
			List<Area> australia = areas.getAreasInContinent(Continent.AUSTRALIA);
			
			for (Area area : areas) {
				for (Area australiaArea : australia) {
					if (area.equals(australiaArea)) {
						area.setControllingPlayer(player1);
					}
				}
			}
			
			assertTrue(areas.controlsContinent(Continent.AUSTRALIA, player1));
			
		} catch (InvalidMappingException e) {
			// this shouldn't happen
			fail("the mapping should work!\r\n" + e.getMessage());
		}
	}
	
	public void testControlsContinentFalse() {
		JsonMapper mapper = new JsonMapper();

		try {			
			AreaList areas = mapper.mapAreasFromJson();

			assertTrue(areas != null && areas.size() > 0);

			Player player1 = new Player("Erika", Color.GREEN, false);
			Player player2 = new Player("Christian", Color.BLUE, false);
			
			List<Area> australia = areas.getAreasInContinent(Continent.AUSTRALIA);
			
			for (Area area : areas) {
				for (int i = 0; i < australia.size();i++) {
					if (area.equals(australia.get(i))) {
						if (i % 2 == 0) {
							area.setControllingPlayer(player1);
						}
						else {
							area.setControllingPlayer(player2);
						}
					}
				}
			}
			
			assertFalse(areas.controlsContinent(Continent.AUSTRALIA, player1));
			
		} catch (InvalidMappingException e) {
			// this shouldn't happen
			fail("the mapping should work!\r\n" + e.getMessage());
		}
	}
	
	public void testCorrectNumberOfAreasForPlayer() {
		JsonMapper mapper = new JsonMapper();

		try {			
			AreaList areas = mapper.mapAreasFromJson();

			assertTrue(areas != null && areas.size() > 0);

			Player player1 = new Player("Erika", Color.GREEN, false);
			
			List<Area> australia = areas.getAreasInContinent(Continent.AUSTRALIA);
			int numberOfAreas = australia.size();
			
			for (Area area : areas) {
				for (Area australiaArea : australia) {
					if (area.equals(australiaArea)) {
						area.setControllingPlayer(player1);
					}
				}
			}
			
			assertTrue(areas.getNumberOfAreasControlledByPlayer(player1) == numberOfAreas);
			
		} catch (InvalidMappingException e) {
			// this shouldn't happen
			fail("the mapping should work!\r\n" + e.getMessage());
		}
	}
	
	public void testWorldDomination() {
		JsonMapper mapper = new JsonMapper();

		try {			
			AreaList areas = mapper.mapAreasFromJson();

			assertTrue(areas != null && areas.size() > 0);

			Player player1 = new Player("Erika", Color.GREEN, false);
			
			List<Area> australia = areas.getAreasInContinent(Continent.AUSTRALIA);			
			
			for (Area area : areas) {
				for (Area australiaArea : australia) {
					if (area.equals(australiaArea)) {
						area.setControllingPlayer(player1);
					}
				}
			}
			
			assertTrue(!areas.isWorldDomination(player1));
			
			for (Area area : areas) {
				area.setControllingPlayer(player1);
			}
			assertTrue(areas.isWorldDomination(player1));
			
		} catch (InvalidMappingException e) {
			// this shouldn't happen
			fail("the mapping should work!\r\n" + e.getMessage());
		}
	}
}
