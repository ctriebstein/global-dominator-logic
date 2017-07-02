package org.ct.gd.logic.handler;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

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

public class GameHandlerTradeCardsTest extends TestCase {
	
	public void testNotPlayersTurn() {
		GameHandler gh = new GameHandlerImpl(new ArrayList<Card>(), null, null);
		Player p1 = constructPlayer();

		p1.setTurn(false);

		try {
			gh.tradeCards(p1, constructValidCardsEqual());
			fail("This player is not allowed to trade cards");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.NOT_PLAYERS_TURN);
		}
	}
	
	public void testNotPlayersPhase() {
		GameHandler gh = new GameHandlerImpl(new ArrayList<Card>(), null, null);
		Player p1 = constructPlayer();

		p1.setTurn(true);
		p1.setPhase(Phase.ATTACK);

		try {
			gh.tradeCards(p1, constructValidCardsEqual());
			fail("This player is not allowed to trade cards");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.WRONG_PHASE_FOR_ACTION);
		}
	}
	
	public void testCardsTradedRight() {
		GameHandler gh = new GameHandlerImpl(new ArrayList<Card>(), null, null);
		Player p1 = constructPlayer();

		p1.setTurn(true);
		p1.setPhase(Phase.TRADE_CARDS);

		try {
			List<List<Card>> cards = new ArrayList<>();
			cards.add(constructValidCardsEqual().get(0));
			cards.add(constructValidCardsNotEqual().get(0));
			int i = gh.tradeCards(p1, cards);
			// first and second amount of cards
			assertTrue(i == 10);
			
			i = gh.tradeCards(p1, constructValidCardsWithWildcards());
			// third amount of cards
			assertTrue(i == 8);
		} catch (GameException e) {
			fail("The mehthod should have returned valid values");
		}
	}
	
	public void testArmiesOutOfListBoundaries() {
		GameHandler gh = new GameHandlerImpl(new ArrayList<Card>(), null, null);
		Player p1 = constructPlayer();

		p1.setTurn(true);
		p1.setPhase(Phase.TRADE_CARDS);

		try {
			// 6 calls should return each value in list
			assertTrue(gh.tradeCards(p1, constructValidCardsEqual()) == 4);
			assertTrue(gh.tradeCards(p1, constructValidCardsEqual()) == 6);
			assertTrue(gh.tradeCards(p1, constructValidCardsEqual()) == 8);
			assertTrue(gh.tradeCards(p1, constructValidCardsEqual()) == 10);
			assertTrue(gh.tradeCards(p1, constructValidCardsEqual()) == 12);
			assertTrue(gh.tradeCards(p1, constructValidCardsEqual()) == 15);
			assertTrue(gh.tradeCards(p1, constructValidCardsEqual()) == 20);
			assertTrue(gh.tradeCards(p1, constructValidCardsEqual()) == 25);
			assertTrue(gh.tradeCards(p1, constructValidCardsEqual()) == 30);						
		} catch (GameException e) {
			fail("The mehthod should have returned valid values");
		}
	}
	
	public void testDeckCorrect() {
		GameHandlerImpl gh = new GameHandlerImpl(new ArrayList<Card>(), null, null);
		Player p1 = constructPlayer();

		p1.setTurn(true);
		p1.setPhase(Phase.TRADE_CARDS);

		try {			
			List<List<Card>> cards = new ArrayList<>();
			cards.add(constructValidCardsEqual().get(0));
			cards.add(constructValidCardsNotEqual().get(0));
			gh.tradeCards(p1, cards);
			gh.tradeCards(p1, constructValidCardsWithWildcards());
			
			assertTrue(gh.getDeck().size() == 9);
		} catch (GameException e) {
			fail("The deck should contain valid values");
		}
	}
	
	public void testCardsTradedWrongWithOneValid() {
		GameHandler gh = new GameHandlerImpl(new ArrayList<Card>(), null, null);
		Player p1 = constructPlayer();

		p1.setTurn(true);
		p1.setPhase(Phase.TRADE_CARDS);

		try {
			List<List<Card>> cards = new ArrayList<>();
			cards.add(new ArrayList<Card>());
			cards.add(constructInvalidCardsCorrectNumber().get(0));
			cards.add(constructValidCardsNotEqual().get(0));
			gh.tradeCards(p1, cards);
			fail("The cards given are crap");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.ILLEGAL_CARD_COMBINATION);
		}
	}
	
	public void testCardsTradedWrongInvalidSize() {
		GameHandler gh = new GameHandlerImpl(new ArrayList<Card>(), null, null);
		Player p1 = constructPlayer();

		p1.setTurn(true);
		p1.setPhase(Phase.TRADE_CARDS);

		try {
			List<List<Card>> cards = new ArrayList<>();
			cards.add(new ArrayList<Card>());
			cards.add(constructInvalidCardsWrongNumber().get(0));
			cards.add(constructValidCardsNotEqual().get(0));
			gh.tradeCards(p1, cards);
			fail("The cards given are crap");
		} catch (GameException e) {
			assertTrue(e.getGameExceptionType() == GameExceptionType.ILLEGAL_CARD_COMBINATION);
		}
	}

	private Player constructPlayer() {
		return new Player("Erika", Color.GREEN, false);
	}

	private List<List<Card>> constructValidCardsEqual() {
		List<Card> cards = new ArrayList<Card>();

		cards.add(new Card(false, new Area("a1", Continent.AFRICA, null), UnitType.ARTILERY));
		cards.add(new Card(false, new Area("a2", Continent.AFRICA, null), UnitType.ARTILERY));
		cards.add(new Card(false, new Area("a3", Continent.AFRICA, null), UnitType.ARTILERY));

		List<List<Card>> cardsList = new ArrayList<>();
		cardsList.add(cards);
		return cardsList;
	}

	private List<List<Card>> constructValidCardsNotEqual() {
		List<Card> cards = new ArrayList<Card>();

		cards.add(new Card(false, new Area("a1", Continent.AFRICA, null), UnitType.CAVALRY));
		cards.add(new Card(false, new Area("a2", Continent.AFRICA, null), UnitType.SOLDIER));
		cards.add(new Card(false, new Area("a3", Continent.AFRICA, null), UnitType.ARTILERY));

		List<List<Card>> cardsList = new ArrayList<>();
		cardsList.add(cards);
		return cardsList;
	}

	private List<List<Card>> constructValidCardsWithWildcards() {
		List<Card> cards = new ArrayList<Card>();

		cards.add(new Card(true, new Area("a1", Continent.AFRICA, null), UnitType.NONE));
		cards.add(new Card(true, new Area("a2", Continent.AFRICA, null), UnitType.NONE));
		cards.add(new Card(false, new Area("a3", Continent.AFRICA, null), UnitType.ARTILERY));

		List<List<Card>> cardsList = new ArrayList<>();
		cardsList.add(cards);
		return cardsList;
	}

	private List<List<Card>> constructInvalidCardsCorrectNumber() {
		List<Card> cards = new ArrayList<Card>();

		cards.add(new Card(false, new Area("a1", Continent.AFRICA, null), UnitType.CAVALRY));
		cards.add(new Card(false, new Area("a2", Continent.AFRICA, null), UnitType.ARTILERY));
		cards.add(new Card(false, new Area("a3", Continent.AFRICA, null), UnitType.ARTILERY));

		List<List<Card>> cardsList = new ArrayList<>();
		cardsList.add(cards);
		return cardsList;
	}

	private List<List<Card>> constructInvalidCardsWrongNumber() {
		List<Card> cards = new ArrayList<Card>();

		cards.add(new Card(false, new Area("a1", Continent.AFRICA, null), UnitType.ARTILERY));
		cards.add(new Card(false, new Area("a2", Continent.AFRICA, null), UnitType.ARTILERY));
		cards.add(new Card(false, new Area("a3", Continent.AFRICA, null), UnitType.ARTILERY));
		cards.add(new Card(false, new Area("a4", Continent.AFRICA, null), UnitType.ARTILERY));

		List<List<Card>> cardsList = new ArrayList<>();
		cardsList.add(cards);
		return cardsList;
	}
}
