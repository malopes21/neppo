package com.neppo.authenticatorserver.model.representation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neppo.authenticatorserver.model.AuthenticationDataResponse;
import com.neppo.authenticatorserver.model.AuthenticationRule;

public class AuthenticationDataResponseRepresentation extends ResourceSupport {

	@JsonInclude(Include.NON_NULL)
	private Long identifier;

	@JsonInclude(Include.NON_NULL)
	private String sessionId;

	@JsonInclude(Include.NON_NULL)
	private String issuer;

	@JsonInclude(Include.NON_NULL)
	private AccountRepresentation account;

	@JsonInclude(Include.NON_NULL)
	private boolean success;

	@JsonInclude(Include.NON_NULL)
	private List<AuthenticationRuleRepresentation> rules;

	public Long getIdentifier() {
		return identifier;
	}

	public AuthenticationDataResponseRepresentation() {

	}

	
	public AuthenticationDataResponseRepresentation(AuthenticationDataResponse authnData) {

		this.identifier = authnData.getId();
		this.sessionId = authnData.getSessionId();
		this.issuer = authnData.getIssuer();
		this.success = authnData.isSucess();

		if (authnData.getAccount() != null) {
			this.account = new AccountRepresentation(authnData.getAccount());
		}
		
		if(authnData.getRules() != null) {
			this.rules = new ArrayList<>();
			for(AuthenticationRule rule: authnData.getRules()) {
				this.rules.add(new AuthenticationRuleRepresentation(rule));
			}
		}
	}

	public static AuthenticationDataResponse build(AuthenticationDataResponseRepresentation representation) {

		AuthenticationDataResponse authnData = new AuthenticationDataResponse();
		authnData.setId(representation.getIdentifier());
		authnData.setSessionId(representation.getSessionId());
		authnData.setIssuer(representation.getIssuer());
		authnData.setSucess(representation.isSuccess());

		if (representation.getAccount() != null) {
			authnData.setAccount(AccountRepresentation.build(representation.getAccount()));
		}

		if(representation.getRules() != null) {
			authnData.setRules(new ArrayList<>());
			for(AuthenticationRuleRepresentation rule: representation.getRules()) {
				authnData.getRules().add(AuthenticationRuleRepresentation.build(rule));
			}
		}
		
		return authnData;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public AccountRepresentation getAccount() {
		return account;
	}

	public void setAccount(AccountRepresentation account) {
		this.account = account;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<AuthenticationRuleRepresentation> getRules() {
		return rules;
	}

	public void setRules(List<AuthenticationRuleRepresentation> rules) {
		this.rules = rules;
	}

}
