package com.neppo.authenticatorserver.domain;

import java.util.List;

public class AuthenticationResponse {

	private Long id;

	private String sessionId;

	private String issuer;

	private Account account;

	private boolean sucess;

	private List<AuthenticationRule> rules;

	private boolean exception;

	private String errorMessage;

	private boolean accountExist;

	private boolean accountValidated;

	private AuthenticationPolicy authnPolicy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public boolean isSucess() {
		return sucess;
	}

	public void setSucess(boolean sucess) {
		this.sucess = sucess;
	}

	public List<AuthenticationRule> getRules() {
		return rules;
	}

	public void setRules(List<AuthenticationRule> rules) {
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

	public AuthenticationPolicy getAuthnPolicy() {
		return authnPolicy;
	}

	public void setAuthnPolicy(AuthenticationPolicy authnPolicy) {
		this.authnPolicy = authnPolicy;
	}

}
