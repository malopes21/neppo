package com.neppo.authenticatorserver.domain.representation.exception;

public class AuthenticationRuleParameterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AuthenticationRuleParameterException(String mensagem) {
		super(mensagem);
	}

	public AuthenticationRuleParameterException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}

}