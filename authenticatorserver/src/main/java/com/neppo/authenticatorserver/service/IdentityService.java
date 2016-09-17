package com.neppo.authenticatorserver.service;

import org.apache.shiro.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.neppo.authenticatorserver.model.Subject;
import com.neppo.authenticatorserver.model.User;

@Service
public class IdentityService {
	
	private Subject subject = new Subject();
	private User user = new User();

	public Subject getSubject() {
		
		return subject;
	}

	public User getUser(String username) {
		user.setUsername(username); 		//malopes
		user.setEmail("malopes21@gmail");
		user.setFirstName("Marcos");
		user.setName("MarcosLopes");
		user.setSureName("Lopes");
		
		return user;
	}

	public CacheManager getCacheManager() {
		return null;
	}

}
