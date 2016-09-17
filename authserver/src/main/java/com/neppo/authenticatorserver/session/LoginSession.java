/**
 * 
 */
package com.neppo.authenticatorserver.session;

import java.util.Date;

/**
 * @author bhlangonijr
 *
 */
public class LoginSession {
	
	private final String id;
	private final String issuer;
	private final Date loginTime;
	private final String subject;
	private final String relayState;
	
	public LoginSession(String id, String issuer, Date loginTime, String subject, String relayState) {
		super();
		this.id = id;
		this.issuer = issuer;
		this.loginTime = loginTime;
		this.subject = subject;
		this.relayState = relayState;
	}
	public String getId() {
		return id;
	}
	public String getIssuer() {
		return issuer;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public String getSubject() {
		return subject;
	}
	public String getRelayState() {
		return relayState;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginSession other = (LoginSession) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
