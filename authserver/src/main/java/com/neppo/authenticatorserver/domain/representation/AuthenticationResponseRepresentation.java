package com.neppo.authenticatorserver.domain.representation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neppo.authenticatorserver.domain.AuthenticationResponse;
import com.neppo.authenticatorserver.domain.AuthenticationRule;

public class AuthenticationResponseRepresentation extends ResourceSupport {

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

	@JsonInclude(Include.NON_NULL)
	private boolean exception;

	@JsonInclude(Include.NON_NULL)
	private String errorMessage;

	@JsonInclude(Include.NON_NULL)
	private boolean accountExist;

	@JsonInclude(Include.NON_NULL)
	private boolean accountValidated;

	@JsonInclude(Include.NON_NULL)
	private AuthenticationPolicyRepresentation authnPolicy;

	public Long getIdentifier() {
		return identifier;
	}

	public AuthenticationResponseRepresentation() {

	}

	public AuthenticationResponseRepresentation(AuthenticationResponse authnData) {

		this.identifier = authnData.getId();
		this.sessionId = authnData.getSessionId();
		this.issuer = authnData.getIssuer();
		this.success = authnData.isSucess();
		this.exception = authnData.isException();
		this.errorMessage = authnData.getErrorMessage();
		this.accountExist = authnData.isAccountExist();
		this.accountValidated = authnData.isAccountValidated();
		
		if(authnData.getAuthnPolicy() != null) {
			this.authnPolicy = new AuthenticationPolicyRepresentation(authnData.getAuthnPolicy());
		}

		if (authnData.getAccount() != null) {
			this.account = new AccountRepresentation(authnData.getAccount());
		}

		if (authnData.getRules() != null) {
			this.rules = new ArrayList<>();
			for (AuthenticationRule rule : authnData.getRules()) {
				this.rules.add(new AuthenticationRuleRepresentation(rule));
			}
		}
	}

	public static AuthenticationResponse build(AuthenticationResponseRepresentation representation) {

		AuthenticationResponse authnData = new AuthenticationResponse();
		authnData.setId(representation.getIdentifier());
		authnData.setSessionId(representation.getSessionId());
		authnData.setIssuer(representation.getIssuer());
		authnData.setSucess(representation.isSuccess());
		authnData.setException(representation.isException());
		authnData.setErrorMessage(representation.getErrorMessage());
		authnData.setAccountExist(representation.isAccountExist());
		authnData.setAccountValidated(representation.isAccountValidated());

		if(representation.getAuthnPolicy() != null) {
			authnData.setAuthnPolicy(AuthenticationPolicyRepresentation.build(representation.getAuthnPolicy()));
		}

		if (representation.getAccount() != null) {
			authnData.setAccount(AccountRepresentation.build(representation.getAccount()));
		}

		if (representation.getRules() != null) {
			authnData.setRules(new ArrayList<>());
			for (AuthenticationRuleRepresentation rule : representation.getRules()) {
				authnData.getRules().add(AuthenticationRuleRepresentation.build(rule));
			}
		}

		return authnData;
	}

	public static AuthenticationResponse build(String representationString)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		AuthenticationResponseRepresentation representation = mapper.readValue(representationString,
				AuthenticationResponseRepresentation.class);
		return build(representation);
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

	public boolean isException() {
		return exception;
	}

	public void setException(boolean exception) {
		this.exception = exception;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isAccountExist() {
		return accountExist;
	}

	public void setAccountExist(boolean accountExist) {
		this.accountExist = accountExist;
	}

	public boolean isAccountValidated() {
		return accountValidated;
	}

	public void setAccountValidated(boolean accountValidated) {
		this.accountValidated = accountValidated;
	}

	public AuthenticationPolicyRepresentation getAuthnPolicy() {
		return authnPolicy;
	}

	public void setAuthnPolicy(AuthenticationPolicyRepresentation authnPolicy) {
		this.authnPolicy = authnPolicy;
	}

}
