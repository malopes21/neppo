package com.neppo.authenticatorserver.saml.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OpenIDConfiguration {
	
	private String endpointUrl;
	private String claimedId;
	
	public String getEndpointUrl() {
		return endpointUrl;
	}
	public void setEndpointUrl(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}
	public String getClaimedId() {
		return claimedId;
	}
	public void setClaimedId(String claimedId) {
		this.claimedId = claimedId;
	}
	
}