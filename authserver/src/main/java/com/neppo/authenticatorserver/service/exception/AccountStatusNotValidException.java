package com.neppo.authenticatorserver.service.exception;

public class AccountStatusNotValidException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccountStatusNotValidException(String mensagem) {
		super(mensagem);
	}
	
	public AccountStatusNotValidException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
	
}