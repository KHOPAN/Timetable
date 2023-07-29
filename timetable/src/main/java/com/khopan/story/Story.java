package com.khopan.story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.khopan.property.Property;
import com.khopan.property.SimpleProperty;

public class Story {
	private final List<String> additionalInformationList;
	private final List<Season> seasonList;

	private String name;

	private Story() {
		this.additionalInformationList = new ArrayList<>();
		this.seasonList = new ArrayList<>();
		this.name = "";
	}

	public Property<String, Story> name() {
		return new SimpleProperty<>(() -> this.name, name -> this.name = name, this).nullable().whenNull("");
	}

	public Story addAdditionalInformation(String additionalInformation) {
		this.additionalInformationList.add(additionalInformation);
		return this;
	}

	public Story removeAdditionalInformation(String additionalInformation) {
		this.additionalInformationList.remove(additionalInformation);
		return this;
	}

	public Story removeAdditionalInformation(int index) {
		this.additionalInformationList.remove(index);
		return this;
	}

	public int additionalInformationSize() {
		return this.additionalInformationList.size();
	}

	public List<String> additionalInformationList() {
		return Collections.unmodifiableList(this.additionalInformationList);
	}

	public Story addSeason(Season season) {
		this.seasonList.add(season);
		return this;
	}

	public Story removeSeason(Season season) {
		this.seasonList.remove(season);
		return this;
	}

	public Story removeSeason(int index) {
		this.seasonList.remove(index);
		return this;
	}

	public int seasonSize() {
		return this.seasonList.size();
	}

	public List<Season> seasonList() {
		return Collections.unmodifiableList(this.seasonList);
	}

	public static Story getInstance() {
		return new Story();
	}

	public static JsonNode serialize(Story story) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode storyNode = mapper.createObjectNode();

		if(story == null) {
			return storyNode;
		}

		storyNode.put("name", story.name);
		ArrayNode seasonListNode = storyNode.putArray("season");

		for(Season season : story.seasonList) {
			seasonListNode.add(Season.serialize(season));
		}

		if(story.additionalInformationList.size() > 0) {
			ArrayNode additionalInformationNode = storyNode.putArray("additional-information");

			for(String additionalInformation : story.additionalInformationList) {
				additionalInformationNode.add(additionalInformation);
			}
		}

		return storyNode;
	}

	public static Story deserialize(JsonNode node) {
		Story story = new Story();

		if(node == null || !node.isObject()) {
			return story;
		}

		if(node.has("name")) {
			story.name = node.get("name").asText();
		}

		if(node.has("season")) {
			for(JsonNode season : node.get("season")) {
				story.seasonList.add(Season.deserialize(season));
			}
		}

		if(node.has("additional-information")) {
			for(JsonNode information : node.get("additional-information")) {
				story.additionalInformationList.add(information.asText());
			}
		}

		return story;
	}
}
