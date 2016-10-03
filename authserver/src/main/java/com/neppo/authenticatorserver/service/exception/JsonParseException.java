package com.neppo.authenticatorserver.service.exception;

public class JsonParseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JsonParseException(String mensagem) {
		super(mensagem);
	}
	
	public JsonParseException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
	
}