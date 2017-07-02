package org.ct.gd.logic.mapper;

import java.util.List;

import junit.framework.TestCase;

import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.mapper.JsonMapper;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.Card;
import org.ct.gd.logic.model.UnitType;
import org.ct.gd.logic.util.AreaList;

public class JsonMapperTest extends TestCase {

	public void testSuccessfullMapping() {
		JsonMapper mapper = new JsonMapper();

		try {
			AreaList areas = mapper.mapAreasFromJson();
			List<Card> cards = mapper.mapCardsFromJson();

			assertTrue(areas != null && areas.size() > 0);
			assertTrue(cards != null && cards.size() > 0);

			assertTrue(cards.size() >= areas.size());
		} catch (InvalidMappingException e) {
			// this shouldn't happen
			fail("the mapping should work!\r\n" + e.getMessage());
		}
	}

	public void testAreaJsonIsCorrect() {
		JsonMapper mapper = new JsonMapper();

		try {
			AreaList areas = mapper.mapAreasFromJson();

			assertTrue(areas != null && areas.size() > 0);

			for (Area area : areas) {
				assertTrue(area != null && area.getNeighbours() != null && area.getNeighbours().size() > 0);
				int numberOfNeighbours = area.getNeighbours().size();
				int numberAreaIsNeighbour = 0;

				for (Area checkArea : areas) {
					if (!checkArea.equals(area)) {
						assertTrue(checkArea != null && checkArea.getNeighbours() != null && checkArea.getNeighbours().size() > 0);

						for (Area neighbour : checkArea.getNeighbours()) {
							if (neighbour.equals(area)) {
								numberAreaIsNeighbour++;
							}
						}
					}
				}

				if (numberOfNeighbours != numberAreaIsNeighbour) {
					fail("The area " + area.toString() + " has " + numberOfNeighbours + " neighbours and is neighbour to " + numberAreaIsNeighbour + " areas. Thats wrong!!!");
				}
			}
		} catch (InvalidMappingException e) {
			// this shouldn't happen
			fail("the mapping should work!\r\n" + e.getMessage());
		}
	}

	public void testCardJsonIsCorrect() {
		JsonMapper mapper = new JsonMapper();

		try {
			List<Card> cards = mapper.mapCardsFromJson();

			assertTrue(cards != null && cards.size() > 0);

			for (Card card : cards) {
				assertTrue(card != null);

				assertTrue((card.getIsWildcard() && card.getArea() == null && card.getUnitType() == UnitType.NONE)
						|| (!card.getIsWildcard() && card.getArea() != null && card.getUnitType() != UnitType.NONE));

				int equalsCounter = 0;
				for (Card checkCard : cards) {
					if (checkCard.equals(card)) {
						equalsCounter++;
					} else {
						if (!card.getIsWildcard() && !checkCard.getIsWildcard()) {
							if (card.getArea().getName().equals(checkCard.getArea().getName())) {
								fail("The card " + card.toString() + " has the same area name as another card. Thats not allowed!!!");
							}
						}
					}
				}

				if (equalsCounter > 1 && !card.getIsWildcard()) {
					fail("The card " + card.toString() + " has " + (equalsCounter - 1) + " dulicates. Thats not allowed!!!");
				}
			}
		} catch (InvalidMappingException e) {
			// this shouldn't happen
			fail("the mapping should work!\r\n" + e.getMessage());
		}
	}

	public void testAreaMatchesCard() {
		JsonMapper mapper = new JsonMapper();

		try {
			AreaList areas = mapper.mapAreasFromJson();
			List<Card> cards = mapper.mapCardsFromJson();

			assertTrue(areas != null && areas.size() > 0);
			assertTrue(cards != null && cards.size() > 0);

			for (Area area : areas) {
				boolean found = false;
				for (Card card : cards) {
					if (!card.getIsWildcard() && card.getArea().equals(area)) {
						found = true;
						break;
					}
				}

				if (!found) {
					fail("The area " + area.toString() + " is not contained in all available cards!");
				}
			}

			for (Card card : cards) {
				boolean found = false;

				if (!card.getIsWildcard()) {
					for (Area area : areas) {
						if (card.getArea().equals(area)) {
							found = true;
							break;
						}
					}

					if (!found) {
						fail("The card " + card.toString() + " refers to an invalid area!");
					}
				}
			}

		} catch (InvalidMappingException e) {
			// this shouldn't happen
			fail("the mapping should work!\r\n" + e.getMessage());
		}
	}
}
