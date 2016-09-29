package com.neppo.authenticatorserver.model;

public class Application {

	private Long id;

	private String name;

	private String description;

	private String uri;

	private String logo;

	private String tags;

	private SamlSsoConfig samlConfig;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public SamlSsoConfig getSamlConfig() {
		return samlConfig;
	}

	public void setSamlConfig(SamlSsoConfig samlConfig) {
		this.samlConfig = samlConfig;
	}

}
