package org.ct.gd.logic.handler;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ct.gd.logic.Game;
import org.ct.gd.logic.GameStatus;
import org.ct.gd.logic.exception.GameException;
import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.AttackResult;
import org.ct.gd.logic.model.Color;
import org.ct.gd.logic.model.Continent;
import org.ct.gd.logic.model.Goal;
import org.ct.gd.logic.model.Phase;
import org.ct.gd.logic.model.Player;
import org.ct.gd.logic.util.AreaList;

public class GameHandlerGoalTest extends TestCase {
	
	public void testControls24AreasWin() {
		try {
			Game game = new Game(constructPlayers(), false);
			game.startGame();
			
			Player winner = null;
			for (Player player : game.getPlayers()) {
				assertTrue(player.getGoal() != Goal.WORLD_DOMINATION);
				
				if (player.getGoal() == Goal.CONQUER_24_AREAS) {
					winner = player;
				}
			}
			assertNotNull(winner);
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			assertTrue(game.getGameHandler().getAreas().getNumberOfAreasControlledByPlayer(winner) < 24);
			
			game.getGameHandler().confirmEndOfPhase(winner);
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.RUNNING);
			assertNull(game.getGameHandler().getWinner());
			
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			
			while (game.getGameHandler().getAreas().getNumberOfAreasControlledByPlayer(winner) < 24) {
				List<Area> areas = game.getGameHandler().getAreas().getAreasControlledByPlayer(winner);
				for (Area area : areas) {
					area.setArmies(1000);
					
					for (Area n : area.getNeighbours()) {
						Area neighbour = game.getGameHandler().getAreas().getByName(n.getName());
						if (!neighbour.getControllingPlayer().equals(winner)) {
							AttackResult ar = game.getGameHandler().attack(winner, area, neighbour, 3);
							game.getGameHandler().defend(neighbour.getControllingPlayer(), ar, 1);
						}
					}
				}
			}
			
			if (!game.getGameHandler().getAreas().isWorldDomination(winner)) {
				game.getGameHandler().confirmEndOfPhase(winner);
			}
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.GAME_OVER);
			assertEquals(winner, game.getGameHandler().getWinner());
		} catch (GameException e) {
			fail("The attack should definitely work");
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		} catch (Exception e) {
			fail("Game initialization shouldn't be a problem here");
		}
	}
	
	public void testControlsEuropeAustraliaAndThird() {
		try {
			Game game = new Game(constructPlayers(), false);
			game.startGame();
			
			Player winner = null;
			for (Player player : game.getPlayers()) {
				assertTrue(player.getGoal() != Goal.WORLD_DOMINATION);
				
				if (player.getGoal() == Goal.CONQUER_EUROPE_AUSTRALIA_AND_THIRD_CONTINENT) {
					winner = player;
				}
			}
			assertNotNull(winner);
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			assertTrue(!game.getGameHandler().getAreas().controlsContinent(Continent.AUSTRALIA, winner) || 
					!game.getGameHandler().getAreas().controlsContinent(Continent.EUROPE, winner) || 
					!game.getGameHandler().getAreas().controlsContinent(Continent.SOUTH_AMERICA, winner));
			
			game.getGameHandler().confirmEndOfPhase(winner);
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.RUNNING);
			assertNull(game.getGameHandler().getWinner());
			
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			
			boolean controlsThree = false;
			while (!controlsThree) {
				AreaList areaList = game.getGameHandler().getAreas();
				List<Area> areas = areaList.getAreasControlledByPlayer(winner);
				for (Area area : areas) {
					area.setArmies(1000);
					
					for (Area n : area.getNeighbours()) {
						Area neighbour = game.getGameHandler().getAreas().getByName(n.getName());
						if (!neighbour.getControllingPlayer().equals(winner)) {
							AttackResult ar = game.getGameHandler().attack(winner, area, neighbour, 3);
							game.getGameHandler().defend(neighbour.getControllingPlayer(), ar, 1);
						}
					}
				}
				
				controlsThree = areaList.controlsContinent(Continent.AUSTRALIA, winner) && areaList.controlsContinent(Continent.EUROPE, winner) && 
						(areaList.controlsContinent(Continent.SOUTH_AMERICA, winner) || areaList.controlsContinent(Continent.NORTH_AMERICA, winner) ||
								areaList.controlsContinent(Continent.ASIA, winner) || areaList.controlsContinent(Continent.AFRICA, winner));
			}
			
			if (!game.getGameHandler().getAreas().isWorldDomination(winner)) {
				game.getGameHandler().confirmEndOfPhase(winner);
			}
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.GAME_OVER);
			assertEquals(winner, game.getGameHandler().getWinner());
		} catch (GameException e) {
			fail("Exception in the game: " + e.getMessage());
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		} catch (Exception e) {
			fail("Game initialization shouldn't be a problem here");
		}
	}
	
	public void testControlsEuropeSouthAmericaAndThird() {
		try {
			Game game = new Game(constructPlayers(), false);
			game.startGame();
			
			Player winner = null;
			for (Player player : game.getPlayers()) {
				assertTrue(player.getGoal() != Goal.WORLD_DOMINATION);
				
				if (player.getGoal() == Goal.CONQUER_EUROPE_SOUTH_AMERICA_AND_THIRD_CONTINENT) {
					winner = player;
				}
			}
			assertNotNull(winner);
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			assertTrue(!game.getGameHandler().getAreas().controlsContinent(Continent.AUSTRALIA, winner) || 
					!game.getGameHandler().getAreas().controlsContinent(Continent.EUROPE, winner) || 
					!game.getGameHandler().getAreas().controlsContinent(Continent.SOUTH_AMERICA, winner));
			
			game.getGameHandler().confirmEndOfPhase(winner);
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.RUNNING);
			assertNull(game.getGameHandler().getWinner());
			
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			
			boolean controlsThree = false;
			while (!controlsThree) {
				AreaList areaList = game.getGameHandler().getAreas();
				List<Area> areas = areaList.getAreasControlledByPlayer(winner);
				for (Area area : areas) {
					area.setArmies(1000);
					
					for (Area n : area.getNeighbours()) {
						Area neighbour = game.getGameHandler().getAreas().getByName(n.getName());
						if (!neighbour.getControllingPlayer().equals(winner)) {
							AttackResult ar = game.getGameHandler().attack(winner, area, neighbour, 3);
							game.getGameHandler().defend(neighbour.getControllingPlayer(), ar, 1);
						}
					}
				}
				
				controlsThree = areaList.controlsContinent(Continent.SOUTH_AMERICA, winner) && areaList.controlsContinent(Continent.EUROPE, winner) && 
						(areaList.controlsContinent(Continent.AUSTRALIA, winner) || areaList.controlsContinent(Continent.NORTH_AMERICA, winner) ||
								areaList.controlsContinent(Continent.ASIA, winner) || areaList.controlsContinent(Continent.AFRICA, winner));
			}
			
			if (!game.getGameHandler().getAreas().isWorldDomination(winner)) {
				game.getGameHandler().confirmEndOfPhase(winner);
			}
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.GAME_OVER);
			assertEquals(winner, game.getGameHandler().getWinner());
		} catch (GameException e) {
			fail("Exception in the game: " + e.getMessage());
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		} catch (Exception e) {
			fail("Game initialization shouldn't be a problem here");
		}
	}
	
	public void testControlsAsiaAndAfrica() {
		try {
			Game game = new Game(constructPlayers(), false);
			game.startGame();
			
			Player winner = null;
			for (Player player : game.getPlayers()) {
				assertTrue(player.getGoal() != Goal.WORLD_DOMINATION);
				
				if (player.getGoal() == Goal.CONQUER_ASIA_AND_AFRICA) {
					winner = player;
				}
			}
			assertNotNull(winner);
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			assertTrue(!game.getGameHandler().getAreas().controlsContinent(Continent.ASIA, winner) || 
					!game.getGameHandler().getAreas().controlsContinent(Continent.AFRICA, winner));
			
			game.getGameHandler().confirmEndOfPhase(winner);
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.RUNNING);
			assertNull(game.getGameHandler().getWinner());
			
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			
			boolean controlsBoth = false;
			while (!controlsBoth) {
				AreaList areaList = game.getGameHandler().getAreas();
				List<Area> areas = areaList.getAreasControlledByPlayer(winner);
				for (Area area : areas) {
					area.setArmies(1000);
					
					for (Area n : area.getNeighbours()) {
						Area neighbour = game.getGameHandler().getAreas().getByName(n.getName());
						if (!neighbour.getControllingPlayer().equals(winner)) {
							AttackResult ar = game.getGameHandler().attack(winner, area, neighbour, 3);
							game.getGameHandler().defend(neighbour.getControllingPlayer(), ar, 1);
						}
					}
				}
				
				controlsBoth = areaList.controlsContinent(Continent.AFRICA, winner) && areaList.controlsContinent(Continent.ASIA, winner);
			}
			
			if (!game.getGameHandler().getAreas().isWorldDomination(winner)) {
				game.getGameHandler().confirmEndOfPhase(winner);
			}
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.GAME_OVER);
			assertEquals(winner, game.getGameHandler().getWinner());
		} catch (GameException e) {
			fail("Exception in the game: " + e.getMessage());
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		} catch (Exception e) {
			fail("Game initialization shouldn't be a problem here");
		}
	}
	
	public void testControlsAsiaAndSouthAmerica() {
		try {
			Game game = new Game(constructPlayers(), false);
			game.startGame();
			
			Player winner = null;
			for (Player player : game.getPlayers()) {
				assertTrue(player.getGoal() != Goal.WORLD_DOMINATION);
				
				if (player.getGoal() == Goal.CONQUER_ASIA_AND_SOUTH_AMERICA) {
					winner = player;
				}
			}
			assertNotNull(winner);
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			assertTrue(!game.getGameHandler().getAreas().controlsContinent(Continent.ASIA, winner) || 
					!game.getGameHandler().getAreas().controlsContinent(Continent.SOUTH_AMERICA, winner));
			
			game.getGameHandler().confirmEndOfPhase(winner);
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.RUNNING);
			assertNull(game.getGameHandler().getWinner());
			
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			
			boolean controlsBoth = false;
			while (!controlsBoth) {
				AreaList areaList = game.getGameHandler().getAreas();
				List<Area> areas = areaList.getAreasControlledByPlayer(winner);
				for (Area area : areas) {
					area.setArmies(1000);
					
					for (Area n : area.getNeighbours()) {
						Area neighbour = game.getGameHandler().getAreas().getByName(n.getName());
						if (!neighbour.getControllingPlayer().equals(winner)) {
							AttackResult ar = game.getGameHandler().attack(winner, area, neighbour, 3);
							game.getGameHandler().defend(neighbour.getControllingPlayer(), ar, 1);
						}
					}
				}
				
				controlsBoth= areaList.controlsContinent(Continent.ASIA, winner) && areaList.controlsContinent(Continent.SOUTH_AMERICA, winner);
			}
			
			if (!game.getGameHandler().getAreas().isWorldDomination(winner)) {
				game.getGameHandler().confirmEndOfPhase(winner);
			}
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.GAME_OVER);
			assertEquals(winner, game.getGameHandler().getWinner());
		} catch (GameException e) {
			fail("Exception in the game: " + e.getMessage());
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		} catch (Exception e) {
			fail("Game initialization shouldn't be a problem here");
		}
	}
	
	public void testControlsNorthAmericaAndAfrica() {
		try {
			Game game = new Game(constructPlayers(), false);
			game.startGame();
			
			Player winner = null;
			for (Player player : game.getPlayers()) {
				assertTrue(player.getGoal() != Goal.WORLD_DOMINATION);
				
				if (player.getGoal() == Goal.CONQUER_NORTH_AMERICA_AND_AFRICA) {
					winner = player;
				}
			}
			assertNotNull(winner);
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			assertTrue(!game.getGameHandler().getAreas().controlsContinent(Continent.NORTH_AMERICA, winner) || 
					!game.getGameHandler().getAreas().controlsContinent(Continent.AFRICA, winner));
			
			game.getGameHandler().confirmEndOfPhase(winner);
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.RUNNING);
			assertNull(game.getGameHandler().getWinner());
			
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			
			boolean controlsBoth = false;
			while (!controlsBoth) {
				AreaList areaList = game.getGameHandler().getAreas();
				List<Area> areas = areaList.getAreasControlledByPlayer(winner);
				for (Area area : areas) {
					area.setArmies(1000);
					
					for (Area n : area.getNeighbours()) {
						Area neighbour = game.getGameHandler().getAreas().getByName(n.getName());
						if (!neighbour.getControllingPlayer().equals(winner)) {
							AttackResult ar = game.getGameHandler().attack(winner, area, neighbour, 3);
							game.getGameHandler().defend(neighbour.getControllingPlayer(), ar, 1);
						}
					}
				}
				
				controlsBoth = areaList.controlsContinent(Continent.NORTH_AMERICA, winner) && areaList.controlsContinent(Continent.AFRICA, winner);
			}
			
			if (!game.getGameHandler().getAreas().isWorldDomination(winner)) {
				game.getGameHandler().confirmEndOfPhase(winner);
			}
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.GAME_OVER);
			assertEquals(winner, game.getGameHandler().getWinner());
		} catch (GameException e) {
			fail("Exception in the game: " + e.getMessage());
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		} catch (Exception e) {
			fail("Game initialization shouldn't be a problem here");
		}
	}
	
	public void testControlsAfricaSouthAmericaAndThird() {
		try {
			Game game = new Game(constructPlayers(), false);
			game.startGame();
			
			Player winner = null;
			for (Player player : game.getPlayers()) {
				assertTrue(player.getGoal() != Goal.WORLD_DOMINATION);
				
				if (player.getGoal() == Goal.CONQUER_AFRICA_SOUTH_AMERICA_AND_THIRD_CONTINENT) {
					winner = player;
				}
			}
			assertNotNull(winner);
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			assertTrue(!game.getGameHandler().getAreas().controlsContinent(Continent.AFRICA, winner) || 
					!game.getGameHandler().getAreas().controlsContinent(Continent.EUROPE, winner) || 
					!game.getGameHandler().getAreas().controlsContinent(Continent.SOUTH_AMERICA, winner));
			
			game.getGameHandler().confirmEndOfPhase(winner);
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.RUNNING);
			assertNull(game.getGameHandler().getWinner());
			
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			
			boolean controlsThree = false;
			while (!controlsThree) {
				AreaList areaList = game.getGameHandler().getAreas();
				List<Area> areas = areaList.getAreasControlledByPlayer(winner);
				for (Area area : areas) {
					area.setArmies(1000);
					
					for (Area n : area.getNeighbours()) {
						Area neighbour = game.getGameHandler().getAreas().getByName(n.getName());
						if (!neighbour.getControllingPlayer().equals(winner)) {
							AttackResult ar = game.getGameHandler().attack(winner, area, neighbour, 3);
							game.getGameHandler().defend(neighbour.getControllingPlayer(), ar, 1);
						}
					}
				}
				
				controlsThree = areaList.controlsContinent(Continent.AFRICA, winner) && areaList.controlsContinent(Continent.SOUTH_AMERICA, winner) && 
						(areaList.controlsContinent(Continent.EUROPE, winner) || areaList.controlsContinent(Continent.NORTH_AMERICA, winner) ||
								areaList.controlsContinent(Continent.ASIA, winner) || areaList.controlsContinent(Continent.AUSTRALIA, winner));
			}
			
			if (!game.getGameHandler().getAreas().isWorldDomination(winner)) {
				game.getGameHandler().confirmEndOfPhase(winner);
			}
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.GAME_OVER);
			assertEquals(winner, game.getGameHandler().getWinner());
		} catch (GameException e) {
			fail("Exception in the game: " + e.getMessage());
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		} catch (Exception e) {
			fail("Game initialization shouldn't be a problem here");
		}
	}
	
	public void testControlsNorthAndSouthAmericaAndAustralia() {
		try {
			Game game = new Game(constructPlayers(), false);
			game.startGame();
			
			Player winner = null;
			for (Player player : game.getPlayers()) {
				assertTrue(player.getGoal() != Goal.WORLD_DOMINATION);
				
				if (player.getGoal() == Goal.CONQUER_NORTH_AMERICA_SOUTH_AMERICA_AND_AUSTRALIA) {
					winner = player;
				}
			}
			assertNotNull(winner);
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			assertTrue(!game.getGameHandler().getAreas().controlsContinent(Continent.AUSTRALIA, winner) || 
					!game.getGameHandler().getAreas().controlsContinent(Continent.NORTH_AMERICA, winner) || 
					!game.getGameHandler().getAreas().controlsContinent(Continent.SOUTH_AMERICA, winner));
			
			game.getGameHandler().confirmEndOfPhase(winner);
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.RUNNING);
			assertNull(game.getGameHandler().getWinner());
			
			winner.setTurn(true);
			winner.setPhase(Phase.ATTACK);
			
			boolean controlsThree = false;
			while (!controlsThree) {
				AreaList areaList = game.getGameHandler().getAreas();
				List<Area> areas = areaList.getAreasControlledByPlayer(winner);
				for (Area area : areas) {
					area.setArmies(1000);
					
					for (Area n : area.getNeighbours()) {
						Area neighbour = game.getGameHandler().getAreas().getByName(n.getName());
						if (!neighbour.getControllingPlayer().equals(winner)) {
							AttackResult ar = game.getGameHandler().attack(winner, area, neighbour, 3);
							game.getGameHandler().defend(neighbour.getControllingPlayer(), ar, 1);
						}
					}
				}
				
				controlsThree = areaList.controlsContinent(Continent.AUSTRALIA, winner) && areaList.controlsContinent(Continent.NORTH_AMERICA, winner) && 
						areaList.controlsContinent(Continent.SOUTH_AMERICA, winner);
			}
			
			if (!game.getGameHandler().getAreas().isWorldDomination(winner)) {
				game.getGameHandler().confirmEndOfPhase(winner);
			}
			assertTrue(game.getGameHandler().getGameStatus() == GameStatus.GAME_OVER);
			assertEquals(winner, game.getGameHandler().getWinner());
		} catch (GameException e) {
			fail("Exception in the game: " + e.getMessage());
		} catch (InvalidMappingException e) {
			fail("Mapping shouldn't be an issue here");
		} catch (Exception e) {
			fail("Game initialization shouldn't be a problem here");
		}
	}
	
	private List<Player> constructPlayers() {
		List<Player> players = new ArrayList<>();
		
		players.add(new Player("p1", Color.GREEN, false));
		players.add(new Player("p2", Color.RED, false));
		players.add(new Player("p3", Color.BLUE, false));
		players.add(new Player("p4", Color.YELLOW, false));
		players.add(new Player("p5", Color.ORANGE, false));
		players.add(new Player("p6", Color.BLACK, false));
		players.add(new Player("p7", Color.PINK, false));
		players.add(new Player("p8", Color.BROWN, false));
		
		return players;
	}
}
