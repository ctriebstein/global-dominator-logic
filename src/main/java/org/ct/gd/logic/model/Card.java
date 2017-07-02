package org.ct.gd.logic.model;

import java.io.Serializable;

/**
 * model class holding all possible cards players can trade
 * 
 * @author ct
 * 
 */
public class Card implements Serializable{
	
	private static final long serialVersionUID = 4886443245113198339L;
	
	private boolean isWildcard;
	private Area area;
	private UnitType unitType;

	/**
	 * default constructor used for json mapping
	 */
	@SuppressWarnings("unused")
	private Card() {

	}

	public Card(boolean isWildcard, Area area, UnitType unitType) {
		this.isWildcard = isWildcard;
		this.area = area;
		this.unitType = unitType;
	}

	public boolean getIsWildcard() {
		return isWildcard;
	}

	public Area getArea() {
		return area;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	@Override
	public boolean equals(Object card) {
		if (card instanceof Card && ((Card) card).getUnitType() == this.unitType && ((Card) card).isWildcard == this.isWildcard) {
			if (((Card) card).getArea() == null && this.area == null) {
				return true;
			} else if ((((Card) card).getArea() != null && this.area == null) || ((Card) card).getArea() == null && this.area != null) {
				return false;
			} else if (((Card) card).getArea().equals(this.getArea())) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return "Wilcard: " + this.isWildcard + "; Area: " + (this.area == null ? "null" : this.area.toString()) + "; UnitType: " + this.unitType.toString();
	}
}
