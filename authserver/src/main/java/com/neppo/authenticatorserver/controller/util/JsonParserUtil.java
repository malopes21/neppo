package com.neppo.authenticatorserver.controller.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neppo.authenticatorserver.service.exception.JsonParseException;

public class JsonParserUtil {

	public static String getJsonParamValue(String json, String param) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root;
		try {
			root = mapper.readTree(json);
			return root.path(param).asText();
		} catch (IOException e) {
			throw new JsonParseException("Invalid param name!");
		}
	}
	
	
	public static String getJsonParamValue(String json, int pos) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root;
		try {
			root = mapper.readTree(json);
			return root.path(pos).asText();
		} catch (IOException e) {
			throw new JsonParseException("Invalid param index!");
		}
	}
	
}
