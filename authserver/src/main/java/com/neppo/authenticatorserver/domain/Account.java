package com.neppo.authenticatorserver.domain;

import java.util.Date;
import java.util.List;

public class Account {

	private Long id;

	private String name;

	private String description;

	private Boolean master;

	private Date expiration;

	private AccountStatus status;

	private String username;

	private String password;

	private User user;

	private Application application;

	private List<AccountDevice> devices;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isMaster() {
		return master;
	}

	public void setMaster(Boolean master) {
		this.master = master;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<AccountDevice> getDevices() {
		return devices;
	}

	public void setDevices(List<AccountDevice> devices) {
		this.devices = devices;
	}


}
