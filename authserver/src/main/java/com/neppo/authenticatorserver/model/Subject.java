package com.neppo.authenticatorserver.model;

import org.apache.shiro.web.session.HttpServletSession;

public class Subject {

	private boolean authenticated = false;
	private HttpServletSession session;
	private String principal;

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public HttpServletSession getSession() {
		return session;
	}

	public void setSession(HttpServletSession session) {
		this.session = session;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public void logout() {

	}

}
