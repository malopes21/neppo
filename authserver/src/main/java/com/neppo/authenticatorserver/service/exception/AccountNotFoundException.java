package com.neppo.authenticatorserver.service.exception;

public class AccountNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccountNotFoundException(String mensagem) {
		super(mensagem);
	}
	
	public AccountNotFoundException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
	
}