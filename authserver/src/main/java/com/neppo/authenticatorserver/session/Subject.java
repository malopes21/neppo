package com.neppo.authenticatorserver.session;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.neppo.authenticatorserver.domain.User;

public class Subject {

	public static final String SUBJECT_ATTRIBUTE = "Subject";
	
	private boolean authenticated = false;
	private User user;
	
	public Subject() {
		
	}

	public Subject(User user){
		this.user = user;
		RequestContextHolder.currentRequestAttributes().setAttribute(SUBJECT_ATTRIBUTE, this, RequestAttributes.SCOPE_SESSION);
		this.authenticated = true;
	}
	
	public static Subject authenticate(User user) {
		return new Subject(user);
	}
	
	public static User getLoggedUser() {
		
		Subject currentSubject = (Subject) RequestContextHolder.currentRequestAttributes()
				.getAttribute(SUBJECT_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
		if(currentSubject == null) {
			return null;
		}
		return 	currentSubject.getUser();			
	}

	
	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public static String getSessionId() {
		return RequestContextHolder.currentRequestAttributes().getSessionId();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void login() {
		authenticated = true;
	}

	public void logout() {
		authenticated = false;
	}

}
