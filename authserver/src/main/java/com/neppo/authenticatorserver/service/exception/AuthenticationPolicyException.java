package com.neppo.authenticatorserver.service.exception;

public class AuthenticationPolicyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuthenticationPolicyException(String mensagem) {
		super(mensagem);
	}
	
	public AuthenticationPolicyException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
	
}