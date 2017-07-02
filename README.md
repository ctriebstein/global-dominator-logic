# global-dominator-logic
Global Dominator is a game following the map and rules of the popular board game "Risk". This module contains the basic game logic and does not include ai players or network capabilities. The rules this logic follows can be downloaded [here](https://www.hasbro.com/common/instruct/risk.pdf).

## Getting started

### Game map and cards
The game map and cards that can be traded throughout the game are stored in two different JSON files in the resources folder. 

The game maps structure is found in `areas.json` and setup as follows

	[
	    {
	        "name": "alaska",
	        "continent": "NORTH_AMERICA",
	        "neighbours": [
	            {
	                "name": "kamtschatka",
	                "continent": "ASIA",
	                "neighbours": null
	            },
	            {
	                "name": "northwestterritories",
	                "continent": "NORTH_AMERICA",
	                "neighbours": null
	            },
	            {
	                "name": "alberta",
	                "continent": "NORTH_AMERICA",
	                "neighbours": null
	            }
	        ]
	    }
	    ...
	]

- The `name` attribute defines the id of an area on the map. 
- The `continent` attribute needs to be defined according to the enum constraints found in `org.ct.gd.logic.model.Continent.java`.
- The `neighbours` array contains all of the areas surrounding areas that are directly connected to this area. The setup of those neighbours is the same only that their `neighbour` attribute can be null.

For each area a card needs to be defined that players can draw throughout the game. This list is separated from the area list since it also contains wildcards. Cards are located in `cards.json`
	
	[
		{
	        "isWildcard": false,
	        "unitType": "ARTILERY",
	        "area": {
	            "name": "westernaustralia",
	            "continent": "AUSTRALIA",
	            "neighbours": null
	        }
	    },
	    {
	        "isWildcard": true,
	        "unitType": "NONE",
	        "area": null
	    }
	]

- A card object has to contain an `area`as defined in `areas.json` unless it is a wildcard (`isWildcard` = true)
- UnitType needs to contain an enum constant from `org.ct.gd.logic.model.UnitType`

Unit tests make sure that for each area a card is defined and vice versa.

### Logic

The game is separated into turns and each turn is separated into several phases. The rules allow 2 to 8 players and define different objectives for winning the game. All methods required for playing the game (like drawing cards, attack, defense, trading cars) are defined and documented in `org.ct.gd.logic.handler.GameHandler`.

The `org.ct.gd.logic.Game` class provides a `GameHandler` object and other necessary operations for a running game. The game can also be started there.

## Sample application
For a sample implementation of the logic look at the console application in `org.ct.gd.logic.Starter.java`. All available commands are always visible by entering `help` in the command prompt.
