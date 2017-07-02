package org.ct.gd.logic.model;

import java.io.Serializable;
import java.util.List;

/**
 * Class representing an area on the game board
 */
public class Area implements Serializable {
	
	private static final long serialVersionUID = -3151681417851856174L;
	
	private String name;
	private Continent continent;
	private List<Area> neighbours;	
	private int armies;
	private Player controllingPlayer;
	
	/**
	 * default constructor used for json mapping
	 */
	@SuppressWarnings("unused")
	private Area() {
		
	}
	
	public Area(String name, Continent continent, List<Area> neighbours) {
		this.name = name;
		this.continent = continent;
		this.neighbours = neighbours;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Continent getContinent() {
		return continent;
	}

	public void setContinent(Continent continent) {
		this.continent = continent;
	}

	public List<Area> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(List<Area> neighbours) {
		this.neighbours = neighbours;
	}

	public Player getControllingPlayer() {
		return controllingPlayer;
	}

	public void setControllingPlayer(Player controllingPlayer) {
		this.controllingPlayer = controllingPlayer;
	}

	public int getArmies() {
		return armies;
	}

	public void setArmies(int armies) {
		this.armies = armies;
	}
	
	@Override
	public boolean equals(Object area) {
		if (area instanceof Area && ((Area) area).getName().equals(this.name) && ((Area) area).getContinent() == this.continent) {
			return true; 
		}

		return false;
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public String toString() {
		return this.name + ";" + this.continent.toString();
	}
}
