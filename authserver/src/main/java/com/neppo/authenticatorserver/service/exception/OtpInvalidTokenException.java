package com.neppo.authenticatorserver.service.exception;

public class OtpInvalidTokenException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OtpInvalidTokenException(String mensagem) {
		super(mensagem);
	}
	
	public OtpInvalidTokenException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
	
}