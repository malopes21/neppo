package com.neppo.authenticatorserver.model;

import javax.servlet.http.HttpSession;

public class Subject {

	private boolean authenticated = false;
	private HttpSession session;
	private String principal;
	private String password;

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void login() {
		authenticated = true;
	}

	public void logout() {
		authenticated = false;
	}

}
