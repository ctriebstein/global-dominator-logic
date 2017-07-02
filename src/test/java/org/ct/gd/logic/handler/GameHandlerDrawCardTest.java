package org.ct.gd.logic.handler;

import java.util.ArrayList;
import java.util.List;

import org.ct.gd.logic.exception.GameException;
import org.ct.gd.logic.exception.GameException.GameExceptionType;
import org.ct.gd.logic.handler.GameHandler;
import org.ct.gd.logic.handler.GameHandlerImpl;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.Card;
import org.ct.gd.logic.model.Color;
import org.ct.gd.logic.model.Continent;
import org.ct.gd.logic.model.Phase;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.model.UnitType;

import junit.framework.TestCase;

public class GameHandlerDrawCardTest extends TestCase {
	
	public void testNotPlayersTurn() {
		GameHandler gh = new GameHandlerImpl(constructAreaList(), null, null);
		Player p1 = constructPlayer();

		p1.setTurn(false);

		try {
			gh.drawCard(p1);
			fail("This player is not allowed to draw cards");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.NOT_PLAYERS_TURN);
		}
	}
	
	public void testNotPlayersPhase() {
		GameHandler gh = new GameHandlerImpl(constructAreaList(), null, null);
		Player p1 = constructPlayer();

		p1.setTurn(true);
		p1.setPhase(Phase.ATTACK);

		try {
			gh.drawCard(p1);
			fail("This player is not allowed to draw cards");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.WRONG_PHASE_FOR_ACTION);
		}
	}
	
	public void testPlayerHasntConqueredAreas() {
		GameHandler gh = new GameHandlerImpl(constructAreaList(), null, null);
		Player p1 = constructPlayer();

		p1.setTurn(true);
		p1.setPhase(Phase.DRAW_CARD);

		try {
			gh.drawCard(p1);
			fail("This player is not allowed to draw cards");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.NO_AREA_CONQUERED);
		}
	}
	
	public void testDrawingCardWorkd() {
		GameHandlerImpl gh = new GameHandlerImpl(constructAreaList(), null, null);
		Player p1 = constructPlayer();

		p1.setTurn(true);
		p1.setPhase(Phase.DRAW_CARD);
		p1.setConqueredAreaThisTurn(true);

		try {
			Card card = gh.drawCard(p1);
			
			assertTrue(card != null);
			assertTrue(gh.getDeck().size() == 5);
			
			for (Card c : gh.getDeck()) {
				if (c.equals(card)) {
					fail("The card was not removed from the deck");
				}
			}
			
		} catch (GameException e) {
			fail("This exception shouldn't occur: " + e.getMessage());
		}
	}

	private Player constructPlayer() {
		return new Player("Erika", Color.GREEN, false);
	}
	
	private List<Card> constructAreaList() {
		List<Card> cards = new ArrayList<Card>();

		cards.add(new Card(false, new Area("a1", Continent.AFRICA, null), UnitType.ARTILERY));
		cards.add(new Card(false, new Area("a2", Continent.AFRICA, null), UnitType.ARTILERY));
		cards.add(new Card(false, new Area("a3", Continent.AFRICA, null), UnitType.ARTILERY));
		cards.add(new Card(false, new Area("a4", Continent.AFRICA, null), UnitType.ARTILERY));
		cards.add(new Card(false, new Area("a5", Continent.AFRICA, null), UnitType.ARTILERY));
		cards.add(new Card(false, new Area("a6", Continent.AFRICA, null), UnitType.ARTILERY));

		return cards;
	}
}
