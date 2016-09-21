package com.neppo.authenticatorserver.model.representation;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class AuthenticationRepresentation extends ResourceSupport {

	@JsonInclude(Include.NON_NULL)
	private Long identifier;

	@JsonInclude(Include.NON_NULL)
	private String username;

	@JsonInclude(Include.NON_NULL)
	private String password;

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
