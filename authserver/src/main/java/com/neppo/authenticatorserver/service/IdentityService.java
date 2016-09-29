package com.neppo.authenticatorserver.service;

//import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neppo.authenticatorserver.model.Account;
import com.neppo.authenticatorserver.model.AuthenticationDataRequest;
import com.neppo.authenticatorserver.model.AuthenticationDataResponse;
import com.neppo.authenticatorserver.model.Subject;
import com.neppo.authenticatorserver.model.User;
import com.neppo.authenticatorserver.model.dao.AuthenticationDataRequestRestDAO;

@Service
public class IdentityService {

	@Autowired
	private AuthenticationDataRequestRestDAO accountDAO;
	
	private Subject subject = new Subject();
	private User user = new User();

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public AuthenticationDataResponse getAuthnDataResponse(AuthenticationDataRequest authnData) {
		
		AuthenticationDataResponse authnDataResponse = accountDAO.findByAuthenticationData(authnData);
		
		if(authnDataResponse != null) {		//TODO: fix it!!!!!!
			user = new User();
			user.setUsername(authnDataResponse.getAccount().getUsername()); 
			user.setEmail(authnDataResponse.getAccount().getDescription());
			user.setFirstName(authnDataResponse.getAccount().getName());
			user.setName(authnDataResponse.getAccount().getName());
			user.setSureName(authnDataResponse.getAccount().getName());
			user.setPassword(authnDataResponse.getAccount().getPassword());
			
			return authnDataResponse;
		}

		return null;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

/*	public CacheManager getCacheManager() {
		return null;
	}*/

}