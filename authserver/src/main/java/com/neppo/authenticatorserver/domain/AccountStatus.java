package com.neppo.authenticatorserver.domain;

public enum AccountStatus {

	NEW("Conta Nova"), 
	ACTIVE("Conta Ativada"), 
	DEACTIVATED("Conta desativada");

	private String value;

	private AccountStatus() {
	}

	private AccountStatus(String msg) {
		this.value = msg;
	}

	public String getValue() {
		return value;
	}

}
