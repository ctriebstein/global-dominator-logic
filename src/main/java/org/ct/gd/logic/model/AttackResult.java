package org.ct.gd.logic.model;

/**
 * class representing an attack executed by a player
 * 
 * @author ct
 * 
 */
public class AttackResult {

	private Area attackingArea;
	private Area defendingArea;
	// the die values for each die used
	private Integer[] attackValues;

	public AttackResult(Area attackingArea, Area defendingArea, Integer[] attackValues) {
		this.attackingArea = attackingArea;
		this.defendingArea = defendingArea;
		this.attackValues = attackValues;
	}

	public Area getAttackingArea() {
		return attackingArea;
	}

	public Area getDefendingArea() {
		return defendingArea;
	}

	public Integer[] getAttackValues() {
		return attackValues;
	}
}
