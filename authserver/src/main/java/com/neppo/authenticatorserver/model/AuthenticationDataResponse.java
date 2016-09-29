package com.neppo.authenticatorserver.model;

import java.util.List;

public class AuthenticationDataResponse {

	private Long id;

	private String sessionId;

	private String issuer;

	private Account account;

	private boolean sucess;

	private List<AuthenticationRule> rules;

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

}
