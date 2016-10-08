package com.neppo.authenticatorserver.domain.representation;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neppo.authenticatorserver.domain.User;

public class UserRepresentation extends ResourceSupport {

	@JsonInclude(Include.NON_NULL)
	private Long identifier;

	@JsonInclude(Include.NON_NULL)
	private String firstName;

	@JsonInclude(Include.NON_NULL)
	private String middleName;

	@JsonInclude(Include.NON_NULL)
	private String lastName;

	@JsonInclude(Include.NON_NULL)
	private String status;

	@JsonInclude(Include.NON_NULL)
	private String email;

	@JsonInclude(Include.NON_NULL)
	private String displayName;

	public UserRepresentation() {
	}

	public UserRepresentation(Long userId) {
		this.identifier = userId;
	}

	public UserRepresentation(User user) {
		this(user, false);
	}

	public UserRepresentation(User user, Boolean expand) {
		
		this.identifier = user.getId();
		this.status = user.getStatus().toString();
		this.displayName = user.getDisplayName();
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.middleName = user.getMiddleName();
	}

	public static User build(UserRepresentation representation) {

		User user = new User();
		user.setDisplayName(representation.getDisplayName());
		user.setEmail(representation.getEmail());
		user.setFirstName(representation.getFirstName());
		user.setLastName(representation.getLastName());
		user.setMiddleName(representation.getMiddleName());
		user.setId(representation.getIdentifier());

		return user;
	}

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}