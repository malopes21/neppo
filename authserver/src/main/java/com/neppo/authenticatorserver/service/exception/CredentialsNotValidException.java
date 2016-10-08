package com.neppo.authenticatorserver.service.exception;

import com.neppo.authenticatorserver.domain.AuthenticationResponse;

public class CredentialsNotValidException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AuthenticationResponse authnResponse;

	public CredentialsNotValidException(String mensagem) {
		super(mensagem);
	}
	
	public CredentialsNotValidException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}

	public CredentialsNotValidException(String mensagem, AuthenticationResponse authnResponse) {
		super(mensagem);
		this.authnResponse = authnResponse;
	}

	public AuthenticationResponse getAuthnResponse() {
		return authnResponse;
	}
	
}