package org.ct.gd.logic.model;

/**
 * class containing detailed information about the result of an attack and a
 * defense
 * 
 * @author et
 * 
 */
public class DefenseResult {

	private Area attackingArea;
	private Area defendingArea;	
	
	private int lostAttackingArmies;
	private int lostDefendingArmies;

	// the die values for each die used
	private Integer[] defenseValues;
	
	private boolean hasConqueredArea;

	public DefenseResult(Area defendingArea, Area attackingArea, Integer[] defenseValues, int lostAttackingArmies, int lostDefendingArmies, boolean hasConqueredArea) {
		this.defendingArea = defendingArea;
		this.attackingArea = attackingArea;
		this.defenseValues = defenseValues;
		this.lostAttackingArmies = lostAttackingArmies;
		this.lostDefendingArmies = lostDefendingArmies;
		this.hasConqueredArea = hasConqueredArea;
	}

	public Area getDefendingArea() {
		return defendingArea;
	}

	public Area getAttackingArea() {
		return attackingArea;
	}

	public Integer[] getDefenseValues() {
		return defenseValues;
	}		
	
	public int getLostAttackingArmies() {
		return lostAttackingArmies;
	}
	
	public int getLostDefendingArmies() {
		return lostDefendingArmies;
	}	
	
	public boolean hasConqueredArea() {
		return hasConqueredArea;
	}	
}
