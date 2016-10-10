package com.neppo.authenticatorserver.service.rules;

import com.neppo.authenticatorserver.domain.Account;
import com.neppo.authenticatorserver.domain.AuthenticationRule;
import com.neppo.authenticatorserver.domain.AuthenticationRuleType;
import com.neppo.authenticatorserver.service.AbstractAuthenticationRule;
import com.neppo.authenticatorserver.service.AuthenticationRuleValidator;

public class AuthenticationRuleCheckMasterAccount extends AbstractAuthenticationRule implements AuthenticationRuleValidator {

	private Account account;
	
	public AuthenticationRuleCheckMasterAccount(AuthenticationRule rule, Account account) {
		this.setRule(rule);
		this.account = account;
	}
	
	@Override
	public void validate() {
		
		if(account.isMaster()) {
			getRule().setValidated(true);
		} 
		
		if(!getRule().isValidated()) {
			getRule().setValidateError("Tipo da Conta (directory account) usada para login não válida nesse contexto!");
		}
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
