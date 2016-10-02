package com.neppo.authenticatorserver.model.representation;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neppo.authenticatorserver.model.AuthenticationRequest;

public class AuthenticationRequestRepresentation extends ResourceSupport {

	@JsonInclude(Include.NON_NULL)
	private Long identifier;

	@JsonInclude(Include.NON_NULL)
	private String username;

	@JsonInclude(Include.NON_NULL)
	private String password;

	@JsonInclude(Include.NON_NULL)
	private String remoteHost;

	@JsonInclude(Include.NON_NULL)
	private String remoteAddr;

	@JsonInclude(Include.NON_NULL)
	private String remoteUser;

	@JsonInclude(Include.NON_NULL)
	private String requestUri;

	@JsonInclude(Include.NON_NULL)
	private String sessionId;

	@JsonInclude(Include.NON_NULL)
	private String userAgent;

	@JsonInclude(Include.NON_NULL)
	private String issuer;

	public Long getIdentifier() {
		return identifier;
	}

	public AuthenticationRequestRepresentation() {

	}

	public AuthenticationRequestRepresentation(AuthenticationRequest authnData) {

		this.identifier = authnData.getId();
		this.username = authnData.getUsername();
		this.password = authnData.getPassword();
		this.remoteHost = authnData.getRemoteHost();
		this.remoteAddr = authnData.getRemoteAddr();
		this.remoteUser = authnData.getRemoteUser();
		this.requestUri = authnData.getRequestUri();
		this.sessionId = authnData.getSessionId();
		this.userAgent = authnData.getUserAgent();
		this.issuer = authnData.getIssuer();
	}

	public static AuthenticationRequest build(AuthenticationRequestRepresentation representation) {

		AuthenticationRequest authnData = new AuthenticationRequest();
		authnData.setId(representation.getIdentifier());
		authnData.setUsername(representation.getUsername());
		authnData.setPassword(representation.getPassword());
		authnData.setRemoteHost(representation.getRemoteHost());
		authnData.setRemoteAddr(representation.getRemoteAddr());
		authnData.setRemoteUser(representation.getRemoteUser());
		authnData.setRequestUri(representation.getRequestUri());
		authnData.setSessionId(representation.getSessionId());
		authnData.setUserAgent(representation.getUserAgent());
		authnData.setIssuer(representation.getIssuer());
		
		return authnData;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getRemoteUser() {
		return remoteUser;
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

}
