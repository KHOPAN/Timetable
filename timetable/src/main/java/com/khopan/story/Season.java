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

public class Season {
	private final List<String> additionalInformationList;
	private final List<Episode> episodeList;

	private String title;
	private String name;

	private Season() {
		this.additionalInformationList = new ArrayList<>();
		this.episodeList = new ArrayList<>();
		this.title = "";
		this.name = "";
	}

	public Property<String, Season> title() {
		return new SimpleProperty<>(() -> this.title, title -> this.title = title, this).nullable().whenNull("");
	}

	public Property<String, Season> name() {
		return new SimpleProperty<>(() -> this.name, name -> this.name = name, this).nullable().whenNull("");
	}

	public Season addAdditionalInformation(String additionalInformation) {
		this.additionalInformationList.add(additionalInformation);
		return this;
	}

	public Season removeAdditionalInformation(String additionalInformation) {
		this.additionalInformationList.remove(additionalInformation);
		return this;
	}

	public Season removeAdditionalInformation(int index) {
		this.additionalInformationList.remove(index);
		return this;
	}

	public int additionalInformationSize() {
		return this.additionalInformationList.size();
	}

	public List<String> additionalInformationList() {
		return Collections.unmodifiableList(this.additionalInformationList);
	}

	public Season addEpisode(Episode episode) {
		this.episodeList.add(episode);
		return this;
	}

	public Season removeEpisode(Episode episode) {
		this.episodeList.remove(episode);
		return this;
	}

	public Season removeEpisode(int index) {
		this.episodeList.remove(index);
		return this;
	}

	public int episodeSize() {
		return this.episodeList.size();
	}

	public List<Episode> episodeList() {
		return Collections.unmodifiableList(this.episodeList);
	}

	public static Season getInstance() {
		return new Season();
	}

	public static JsonNode serialize(Season season) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode seasonNode = mapper.createObjectNode();

		if(season == null) {
			return seasonNode;
		}

		seasonNode.put("title", season.title);
		seasonNode.put("name", season.name);
		ArrayNode episodeListNode = seasonNode.putArray("episode");

		for(Episode episode : season.episodeList) {
			episodeListNode.add(Episode.serialize(episode));
		}

		if(season.additionalInformationList.size() > 0) {
			ArrayNode additionalInformationNode = seasonNode.putArray("additional-information");

			for(String additionalInformation : season.additionalInformationList) {
				additionalInformationNode.add(additionalInformation);
			}
		}

		return seasonNode;
	}

	public static Season deserialize(JsonNode node) {
		Season season = new Season();

		if(node == null || !node.isObject()) {
			return season;
		}

		if(node.has("title")) {
			season.title = node.get("title").asText();
		}

		if(node.has("name")) {
			season.name = node.get("name").asText();
		}

		if(node.has("episode")) {
			for(JsonNode episode : node.get("episode")) {
				season.episodeList.add(Episode.deserialize(episode));
			}
		}

		if(node.has("additional-information")) {
			for(JsonNode information : node.get("additional-information")) {
				season.additionalInformationList.add(information.asText());
			}
		}

		return season;
	}
}
