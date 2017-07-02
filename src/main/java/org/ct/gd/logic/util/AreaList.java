package org.ct.gd.logic.util;

import java.util.ArrayList;
import java.util.List;

import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.Continent;
import org.ct.gd.logic.model.Player;

/**
 * custom implementation of an arraylist to provide specialized area related
 * methods - therefore the contained objects are always areas
 * 
 * @author ct
 * 
 */
public class AreaList extends ArrayList<Area> {

	private static final long serialVersionUID = -8420837417463594917L;

	/**
	 * checks if a player controls given continent
	 * 
	 * @param continent
	 *            the continent the player is questioned to control
	 * @param player
	 *            the player used for the check
	 * @return true, if the player controls given continent (All areas within),
	 *         false otherwise
	 */
	public boolean controlsContinent(Continent continent, Player player) {
		if (continent == null || player == null || this.size() == 0) {
			return false;
		}

		for (Area area : this) {
			if (area == null || area.getContinent() == null) {
				return false;
			}

			if (area.getContinent() == continent
					&& (area.getControllingPlayer() == null || !area
							.getControllingPlayer().equals(player))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * searches all areas in the given continent
	 * 
	 * @param continent
	 *            the continent all areas should be returned for
	 * @return all areas within a given continent, null if no areas were found
	 */
	public List<Area> getAreasInContinent(Continent continent) {
		if (continent == null || this.size() == 0) {
			return null;
		}

		List<Area> areas = new ArrayList<>();

		for (Area area : this) {
			if (area != null && area.getContinent() != null
					&& area.getContinent() == continent) {
				areas.add(area);
			}
		}

		return areas;
	}

	/**
	 * returns the amount of areas under control of the given player
	 * 
	 * @param player
	 *            the player for whom the areas should be determined
	 * @return the number of areas the player actually controls
	 */
	public int getNumberOfAreasControlledByPlayer(Player player) {
		int numberOfAreas = 0;

		for (Area area : this) {
			if (area != null && area.getControllingPlayer() != null
					&& area.getControllingPlayer().equals(player)) {
				numberOfAreas++;
			}
		}

		return numberOfAreas;
	}

	/**
	 * check if the given player has reached the ultimate goal: world
	 * domination. Meaning controlling every area on the map. <br>
	 * Muhahahahaha
	 * 
	 * @param player
	 *            the player to check world domination for
	 * @return true if world domination was achieved, false otherwise
	 */
	public boolean isWorldDomination(Player player) {
		for (Area area : this) {
			if (area == null || area.getControllingPlayer() == null
					|| !area.getControllingPlayer().equals(player)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * gets an area by its name
	 * 
	 * @param areaName
	 * @return if an area with the given name exists it is returned - null
	 *         otherwise
	 */
	public Area getByName(String areaName) {
		for (Area area : this) {
			if (area.getName().equals(areaName)) {
				return area;
			}
		}

		return null;
	}

	/**
	 * retrieves a list of areas that are currently controlled by the given
	 * player
	 * 
	 * @param player
	 *            the player to get all areas for
	 * @return a list of areas controlled by the player
	 */
	public List<Area> getAreasControlledByPlayer(Player player) {
		List<Area> areas = new ArrayList<>();
		for (Area area : this) {
			if (area != null && area.getControllingPlayer() != null
					&& area.getControllingPlayer().equals(player)) {
				areas.add(area);
			}
		}

		return areas;
	}
}
