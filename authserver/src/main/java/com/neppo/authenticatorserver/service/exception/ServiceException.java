package com.neppo.authenticatorserver.service.exception;

public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceException(String mensagem) {
		super(mensagem);
	}
	
	public ServiceException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
	
}