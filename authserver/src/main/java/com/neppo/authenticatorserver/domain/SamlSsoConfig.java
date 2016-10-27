package com.neppo.authenticatorserver.domain;

public class SamlSsoConfig {

	private String issuer;

	private String assertionConsumerServiceURL;

	private String destination;

	public SamlSsoConfig() {
		super();
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getAssertionConsumerServiceURL() {
		return assertionConsumerServiceURL;
	}

	public void setAssertionConsumerServiceURL(String assertionConsumerServiceURL) {
		this.assertionConsumerServiceURL = assertionConsumerServiceURL;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((issuer == null) ? 0 : issuer.hashCode());
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
		SamlSsoConfig other = (SamlSsoConfig) obj;
		if (issuer == null) {
			if (other.issuer != null)
				return false;
		} else if (!issuer.equals(other.issuer))
			return false;
		return true;
	}

}
