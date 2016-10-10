package com.neppo.authenticatorserver.domain.representation;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neppo.authenticatorserver.domain.Account;
import com.neppo.authenticatorserver.domain.AccountStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountRepresentation extends ResourceSupport {

	@JsonInclude(Include.NON_NULL)
	private Long identifier;

	@JsonInclude(Include.NON_NULL)
	private String name;

	@JsonInclude(Include.NON_NULL)
	private String description;

	@JsonInclude(Include.NON_NULL)
	private Boolean master;

	@JsonInclude(Include.NON_NULL)
	private ApplicationRepresentation application;

	@JsonInclude(Include.NON_NULL)
	private UserRepresentation user;

	@JsonInclude(Include.NON_NULL)
	private String username;

	@JsonInclude(Include.NON_NULL)
	private String password;

	@JsonInclude(Include.NON_NULL)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expiration;

	@JsonInclude(Include.NON_NULL)
	private String status;

	public AccountRepresentation() {
	}

	public AccountRepresentation(Account account) {
		this(account, false);
	}

	public AccountRepresentation(Account account, Boolean expand) {

		this.identifier = account.getId();
		this.name = account.getName();
		this.description = account.getDescription();
		this.master = account.isMaster();
		this.username = account.getUsername();
		
		if (account.getUser() != null) {
			if(expand) {
				this.user = new UserRepresentation(account.getUser().getId());	
			} 
		}

		if (account.getApplication() != null) {
			if(expand) {
				this.application = new ApplicationRepresentation(account.getApplication());
			} 
		}
		
		this.expiration = account.getExpiration();
		this.status = account.getStatus().toString();
	}

	
	public static Account build(AccountRepresentation representation) {

		Account account = new Account();
		account.setId(representation.getIdentifier());
		account.setName(representation.getName());
		account.setDescription(representation.getDescription());
		account.setMaster(representation.getMaster());
		
		if(representation.getStatus() != null) {
			account.setStatus(AccountStatus.valueOf(representation.getStatus()));
		}
		
		if (representation.getUser() != null) {
			account.setUser(UserRepresentation.build(representation.getUser()));
		}
		
		if(representation.getApplication() != null) {
			account.setApplication(ApplicationRepresentation.build(representation.getApplication()));
		}

		account.setUsername(representation.getUsername());
		account.setPassword(representation.getPassword());
		account.setExpiration(representation.getExpiration());
		
		return account;
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

	public Boolean getMaster() {
		return master;
	}

	public void setMaster(Boolean master) {
		this.master = master;
	}

	public ApplicationRepresentation getApplication() {
		return application;
	}

	public void setApplication(ApplicationRepresentation application) {
		this.application = application;
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

	public UserRepresentation getUser() {
		return user;
	}

	public void setUser(UserRepresentation user) {
		this.user = user;
	}

}