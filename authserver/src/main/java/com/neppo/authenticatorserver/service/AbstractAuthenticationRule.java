package com.neppo.authenticatorserver.service;

import com.neppo.authenticatorserver.domain.AuthenticationRule;

public abstract class AbstractAuthenticationRule {

	private AuthenticationRule rule;

	public AuthenticationRule getRule() {
		return rule;
	}

	public void setRule(AuthenticationRule rule) {
		this.rule = rule;
	}
	
	public boolean isValid() {
		return getRule().isValidated();
	}
	
	public String getValidatedError() {
		return rule.getValidateError();
	}
	
}
