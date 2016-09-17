package com.neppo.authenticatorserver.model;

import org.apache.shiro.web.session.HttpServletSession;

public class Subject {

	private boolean authenticated = false;

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public String getPrincipal() {
		return "malopes";
	}

	public HttpServletSession getSession() {
		return null;
	}

	public void logout() {

	}

}
