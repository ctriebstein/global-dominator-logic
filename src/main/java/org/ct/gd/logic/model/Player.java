package org.ct.gd.logic.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * object class determining all relevant information about a single player
 * 
 * @author ct
 * 
 */
public class Player implements Serializable {
	
	private static final long serialVersionUID = -3814319675372711248L;
	
	private boolean turn;
	private String name;
	private Color color;
	private Phase phase;
	private Goal goal;
	private boolean aiPlayer;
	private boolean conqueredAreaThisTurn;
	private int noOfReinforcements;
	private List<Card> cards;

	public Player(String name, Color color, boolean aiPlayer) {
		this.name = name;
		this.setTurn(false);
		this.color = color;
		this.setGoal(Goal.WORLD_DOMINATION);
		this.aiPlayer = aiPlayer;
		this.cards = new ArrayList<Card>();
	}

	public boolean isTurn() {
		return turn;
	}

	public void setTurn(boolean turn) {
		this.turn = turn;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public Goal getGoal() {
		return goal;
	}

	public void setGoal(Goal goal) {
		this.goal = goal;
	}

	public boolean isAiPlayer() {
		return aiPlayer;
	}
	
	public boolean hasConqueredAreaThisTurn() {
		return conqueredAreaThisTurn;
	}

	public void setConqueredAreaThisTurn(boolean conqueredAreaThisTurn) {
		this.conqueredAreaThisTurn = conqueredAreaThisTurn;
	}

	public int getNoOfReinforcements() {
		return noOfReinforcements;
	}

	public void setNoOfReinforcements(int noOfReinforcements) {
		this.noOfReinforcements = noOfReinforcements;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	@Override
	public boolean equals(Object player) {
		if (player instanceof Player) {
			if (player != null && ((Player) player).getName().equals(this.name) && ((Player) player).getColor() == this.color) {
				return true;
			}
		}

		return false;
	}	
	
	@Override
	public String toString() {
		return this.name + " " + this.color;
	}
}
