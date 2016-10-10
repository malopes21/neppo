package com.neppo.authenticatorserver.service.rules;

import java.time.LocalTime;

import com.neppo.authenticatorserver.domain.AuthenticationRule;
import com.neppo.authenticatorserver.domain.AuthenticationRuleType;
import com.neppo.authenticatorserver.domain.representation.exception.AuthenticationRuleParameterException;
import com.neppo.authenticatorserver.service.AbstractAuthenticationRule;
import com.neppo.authenticatorserver.service.AuthenticationRuleValidator;

public class AuthenticationRuleCheckValidSchedule extends AbstractAuthenticationRule implements AuthenticationRuleValidator {

	public AuthenticationRuleCheckValidSchedule(AuthenticationRule rule) {
		this.setRule(rule);
	}
	
	@Override
	public void validate() {
		
		String sInitial = (String) getRule().getParams().get("initial");
		String sEnd = (String) getRule().getParams().get("end");
		
		LocalTime initial = LocalTime.parse(sInitial);
		LocalTime end = LocalTime.parse(sEnd);
		LocalTime now = LocalTime.now();
		if(now.isAfter(initial) && now.isBefore(end)) {
			getRule().setValidated(true);
		}
		
		if(!getRule().isValidated()) {
			getRule().setValidateError("Horário não válido para login nesse contexto!");
		}
	}

	@Override
	public AuthenticationRuleType getType() {
		return getRule().getType();
	}

	@Override
	public void validateParams() {
		
		try {
			
			String sInitial = (String) getRule().getParams().get("initial");
			String sEnd = (String) getRule().getParams().get("end");
			
			LocalTime.parse(sInitial);
			LocalTime.parse(sEnd);
			LocalTime.now();
			
		}catch(Exception ex) {
			
			throw new AuthenticationRuleParameterException(ex.getMessage());
		}
	}

}
