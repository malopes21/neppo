package com.neppo.authenticatorserver.domain.representation;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neppo.authenticatorserver.domain.SamlSsoConfig;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SamlSsoConfigRepresentation extends ResourceSupport {

	@JsonInclude(Include.NON_NULL)
	private Long identifier;
	
	@JsonInclude(Include.NON_NULL)
	private String issuer;

	@JsonInclude(Include.NON_NULL)
	private String assertionConsumerServiceURL;

	@JsonInclude(Include.NON_NULL)
	private String destination;

	public SamlSsoConfigRepresentation() {
		super();
	}
	
	public SamlSsoConfigRepresentation(SamlSsoConfig config, Boolean expand) {

		this.issuer = config.getIssuer();
		this.assertionConsumerServiceURL = config.getAssertionConsumerServiceURL();
		this.destination = config.getDestination();
	}
	
	public static SamlSsoConfig build(SamlSsoConfigRepresentation representation) {

		SamlSsoConfig config = new SamlSsoConfig();
		config.setIssuer(representation.getIssuer());
		config.setAssertionConsumerServiceURL(representation.getAssertionConsumerServiceURL());
		config.setDestination(representation.getDestination());
		return config;
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
		SamlSsoConfigRepresentation other = (SamlSsoConfigRepresentation) obj;
		if (issuer == null) {
			if (other.issuer != null)
				return false;
		} else if (!issuer.equals(other.issuer))
			return false;
		return true;
	}

}
