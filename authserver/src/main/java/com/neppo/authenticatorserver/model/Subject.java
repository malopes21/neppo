package com.neppo.authenticatorserver.model;

import javax.servlet.http.HttpSession;

//import org.apache.shiro.web.session.HttpServletSession;

public class Subject {

	private boolean authenticated = false;
	//private HttpServletSession session;
	private HttpSession session;
	private String principal;

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

	public void logout() {

	}

}
