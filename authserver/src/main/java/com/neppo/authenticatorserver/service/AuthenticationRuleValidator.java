package com.neppo.authenticatorserver.service;

import com.neppo.authenticatorserver.domain.AuthenticationRuleType;

public interface AuthenticationRuleValidator {
	
	public void validate();
	
	public AuthenticationRuleType getType();
	
	public boolean isValid();
	
	public void validateParams();
}
