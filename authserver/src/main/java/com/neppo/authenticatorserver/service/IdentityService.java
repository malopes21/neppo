package com.neppo.authenticatorserver.service;

//import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neppo.authenticatorserver.model.Account;
import com.neppo.authenticatorserver.model.Subject;
import com.neppo.authenticatorserver.model.User;
import com.neppo.authenticatorserver.model.dao.AccountDAO;

@Service
public class IdentityService {

	@Autowired
	private AccountDAO accountDAO;
	
	private Subject subject = new Subject();
	private User user = new User();

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public User getUser(String username) {
		
		Account account = accountDAO.findByUsername(username);
		
		if(account != null) {
			user = new User();
			user.setUsername(username); 
			user.setEmail(account.getDescription());
			user.setFirstName(account.getName());
			user.setName(account.getName());
			user.setSureName(account.getName());
			user.setPassword("123");
			return user;
		}
		
/*		if(username != null && username.equals("malopes")) {
			user.setUsername(username); 
			user.setEmail("malopes21@gmail");
			user.setFirstName("Marcos");
			user.setName("MarcosLopes");
			user.setSureName("Lopes");
			user.setPassword("123");
			return user;
		}*/

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