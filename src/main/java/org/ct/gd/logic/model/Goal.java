package org.ct.gd.logic.model;

/**
 * enumeration containing all possible player goals in the game
 * 
 * @author ct
 * 
 */
public enum Goal {
	WORLD_DOMINATION (0),
	CONQUER_EUROPE_AUSTRALIA_AND_THIRD_CONTINENT (1),
	CONQUER_EUROPE_SOUTH_AMERICA_AND_THIRD_CONTINENT (2),
	CONQUER_ASIA_AND_AFRICA (3),
	CONQUER_ASIA_AND_SOUTH_AMERICA (4),
	CONQUER_NORTH_AMERICA_AND_AFRICA (5),
	CONQUER_24_AREAS (6),
	CONQUER_AFRICA_SOUTH_AMERICA_AND_THIRD_CONTINENT (7),
	CONQUER_NORTH_AMERICA_SOUTH_AMERICA_AND_AUSTRALIA (8);
	
	private int number;
	
	private Goal(int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return this.number;
	}
	
	/**
	 * @return the number of goals in that goal list
	 */
	public static int getNumberOfGoals() {
		return values().length;
	}
	
	/**
	 * gets a goal be its unique number
	 * @param goalNumber the number assigned to the goal
	 * @return a determined goal, World Domination (0) if the number was not found
	 */
	public static Goal getGoalByGoalNumber(int goalNumber) {
		for (Goal g : values()) {
			if (g.getNumber() == goalNumber) {
				return g;
			}
		}
		
		return WORLD_DOMINATION;
	}
}
