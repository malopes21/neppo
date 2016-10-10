package com.neppo.authenticatorserver.service.rules;

import java.util.List;

import com.neppo.authenticatorserver.domain.Account;
import com.neppo.authenticatorserver.domain.AccountDevice;
import com.neppo.authenticatorserver.domain.AuthenticationRule;
import com.neppo.authenticatorserver.domain.AuthenticationRuleType;
import com.neppo.authenticatorserver.service.AbstractAuthenticationRule;
import com.neppo.authenticatorserver.service.AuthenticationRuleValidator;

public class AuthenticationRuleCheckValidDeviceOrigin extends AbstractAuthenticationRule implements AuthenticationRuleValidator {

	private Account account;
	
	public AuthenticationRuleCheckValidDeviceOrigin(AuthenticationRule rule, Account account) {
		this.setRule(rule);
		this.account = account;
	}
	
	@Override
	public void validate() {
		
		boolean useValidDevices = true;
		if(useValidDevices) {
			List<AccountDevice> devices = account.getDevices();
			System.out.println(devices);
		}
		getRule().setValidated(true);

		if(!getRule().isValidated()) {
			getRule().setValidateError("Dispositivo remoto não válido para login nesse contexto!");
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
