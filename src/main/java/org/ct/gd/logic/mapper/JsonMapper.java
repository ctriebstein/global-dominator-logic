package org.ct.gd.logic.mapper;

import java.io.InputStreamReader;
import java.util.List;

import org.ct.gd.logic.exception.InvalidMappingException;
import org.ct.gd.logic.model.Area;
import org.ct.gd.logic.model.Card;
import org.ct.gd.logic.util.AreaList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * class mapping the resource json file of areas against the given data model
 * 
 * @author ct
 * 
 */
public class JsonMapper {

	private static final String AREA_RESOURCE = "areas.json";
	private static final String CARDS_RESOURCE = "cards.json";
	
	/**
	 * maps all given areas in the resource file area.json to a list of areas
	 * 
	 * @return the list of areas found in the given json
	 * @throws InvalidMappingException
	 *             in case the json file is not compatible to the requested area
	 *             mapping
	 */
	public AreaList mapAreasFromJson() throws InvalidMappingException {
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {			
			InputStreamReader isr = new InputStreamReader(getClass().getClassLoader().getResource(AREA_RESOURCE).openStream());			
			
			return objectMapper.readValue(isr, TypeFactory.defaultInstance().constructCollectionType(AreaList.class, Area.class));			
		}
		catch (Exception e) {
			throw new InvalidMappingException("Invalid mapping from json to object.", e);
		}
	}	
	
	/**
	 * maps all given cards in the resource file card.json to a list of cards
	 * 
	 * @return the list of cards found in the given json
	 * @throws InvalidMappingException
	 *             in case the json file is not compatible to the requested cards
	 *             mapping
	 */
	public List<Card> mapCardsFromJson() throws InvalidMappingException {
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {			
			InputStreamReader isr = new InputStreamReader(getClass().getClassLoader().getResource(CARDS_RESOURCE).openStream());			
			
			return objectMapper.readValue(isr, TypeFactory.defaultInstance().constructCollectionType(List.class, Card.class));			
		}
		catch (Exception e) {
			throw new InvalidMappingException("Invalid mapping from json to object.", e);
		}
	}	
}
