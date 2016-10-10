package com.neppo.authenticatorserver.service.rules;

import com.neppo.authenticatorserver.domain.AuthenticationRule;
import com.neppo.authenticatorserver.domain.AuthenticationRuleType;
import com.neppo.authenticatorserver.service.AbstractAuthenticationRule;
import com.neppo.authenticatorserver.service.AuthenticationRuleValidator;

public class AuthenticationRuleCheckMFA extends AbstractAuthenticationRule implements AuthenticationRuleValidator {

	
	public AuthenticationRuleCheckMFA(AuthenticationRule rule) {
		this.setRule(rule);
	}
	
	@Override
	public void validate() {
		
		getRule().setValidated(true);
	}

	@Override
	public AuthenticationRuleType getType() {
		return getRule().getType();
	}

	@Override
	public void validateParams() {
		//nothing to do here
	}
	
}
