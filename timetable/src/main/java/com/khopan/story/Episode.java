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

public class Episode {
	private final List<String> additionalInformationList;

	private String title;
	private String name;
	private String content;

	private Episode() {
		this.additionalInformationList = new ArrayList<>();
	}

	public Property<String, Episode> title() {
		return new SimpleProperty<>(() -> this.title, title -> this.title = title, this).nullable().whenNull("");
	}

	public Property<String, Episode> name() {
		return new SimpleProperty<>(() -> this.name, name -> this.name = name, this).nullable().whenNull("");
	}

	public Property<String, Episode> content() {
		return new SimpleProperty<>(() -> this.content, content -> this.content = content, this).nullable().whenNull("");
	}

	public Episode format() {
		String[] words = this.content.split("\\s+");
		StringBuilder lineBuffer = new StringBuilder();
		String lastWord = null;

		for(String word : words) {
			if(lastWord == null) {
				lineBuffer.append(word);
			} else {
				String space = " ";

				if(this.isASCII(lastWord, false) && this.isASCII(word, true)) {
					space = "";
				}

				lineBuffer.append(space).append(word);
			}

			lastWord = word;
		}

		this.content = lineBuffer.toString();
		return this;
	}

	public Episode addAdditionalInformation(String additionalInformation) {
		this.additionalInformationList.add(additionalInformation);
		return this;
	}

	public Episode removeAdditionalInformation(String additionalInformation) {
		this.additionalInformationList.remove(additionalInformation);
		return this;
	}

	public Episode removeAdditionalInformation(int index) {
		this.additionalInformationList.remove(index);
		return this;
	}

	public int additionalInformationSize() {
		return this.additionalInformationList.size();
	}

	public List<String> additionalInformationList() {
		return Collections.unmodifiableList(this.additionalInformationList);
	}

	private boolean isASCII(String text, boolean first) {
		return ((int) (first ? text.charAt(0) : text.charAt(text.length() - 1))) >= 128;
	}

	public static Episode getInstance() {
		return new Episode();
	}

	public static JsonNode serialize(Episode episode) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode episodeNode = mapper.createObjectNode();

		if(episode == null) {
			return episodeNode;
		}

		episodeNode.put("title", episode.title);
		episodeNode.put("name", episode.name);
		episodeNode.put("content", episode.content);

		if(episode.additionalInformationList.size() > 0) {
			ArrayNode additionalInformationNode = episodeNode.putArray("additional-information");

			for(String additionalInformation : episode.additionalInformationList) {
				additionalInformationNode.add(additionalInformation);
			}
		}

		return episodeNode;
	}

	public static Episode deserialize(JsonNode node) {
		Episode episode = new Episode();

		if(node == null || !node.isObject()) {
			return episode;
		}

		if(node.has("title")) {
			episode.title = node.get("title").asText();
		}

		if(node.has("name")) {
			episode.name = node.get("name").asText();
		}

		if(node.has("content")) {
			episode.content = node.get("content").asText();
		}

		if(node.has("additional-information")) {
			for(JsonNode information : node.get("additional-information")) {
				episode.additionalInformationList.add(information.asText());
			}
		}

		return episode;
	}
}
