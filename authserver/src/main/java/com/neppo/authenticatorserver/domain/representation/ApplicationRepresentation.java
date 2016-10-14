package com.neppo.authenticatorserver.domain.representation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neppo.authenticatorserver.domain.Application;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationRepresentation extends ResourceSupport {

	@JsonInclude(Include.NON_NULL)
	private Long identifier;

	@JsonInclude(Include.NON_NULL)
	private String name;

	@JsonInclude(Include.NON_NULL)
	private String description;

	@JsonInclude(Include.NON_NULL)
	private List<String> tags;

	public ApplicationRepresentation() {
	}

	public ApplicationRepresentation(Application application) {
		this(application, false);
	}

	public ApplicationRepresentation(Application application, Boolean expand) {

		this.identifier = application.getId();
		this.name = application.getName();
		this.description = application.getDescription();

		if (application.getTags() != null) {
			List<String> listTags = new ArrayList<>();
			String[] localTags = application.getTags().split(";");
			for (String tag : localTags) {
				listTags.add(tag);
			}
			this.tags = listTags;
		}

	}

	public static Application build(ApplicationRepresentation representation) {

		Application application = new Application();
		application.setId(representation.getIdentifier());
		application.setName(representation.getName());
		application.setDescription(representation.getDescription());

		if (representation.getTags() != null) {
			StringBuilder sb = new StringBuilder();
			for (String tag : representation.getTags()) {
				sb.append(tag);
				sb.append(";");
			}
			String tagsString = sb.toString();
			if (tagsString.length() > 254) {
				tagsString = tagsString.substring(0, 254);
			}
			application.setTags(tagsString);
		}

		return application;
	}

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}