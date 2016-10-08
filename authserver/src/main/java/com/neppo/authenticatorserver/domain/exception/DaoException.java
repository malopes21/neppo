package com.neppo.authenticatorserver.domain.exception;

public class DaoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DaoException() {
		super("AuthServer Dao Exception.");
	}
	
	public DaoException(String mensagem) {
		super(mensagem);
	}
	
	public DaoException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}

}
