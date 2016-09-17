package com.neppo.authenticatorserver.model;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

public class Account extends ResourceSupport {

	private Long identifier;
	private String name;
	private String description;
	private Boolean master;
	private Date expiration;
	private String status;
	private String username;

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

	public Boolean getMaster() {
		return master;
	}

	public void setMaster(Boolean master) {
		this.master = master;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "Account [identifier=" + identifier + ", name=" + name + ", description=" + description + ", master="
				+ master + ", expiration=" + expiration + ", status=" + status + ", username=" + username + "]";
	}

}
