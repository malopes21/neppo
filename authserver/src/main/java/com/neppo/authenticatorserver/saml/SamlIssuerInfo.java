/**
 * 
 */
package com.neppo.authenticatorserver.saml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author bhlangonijr
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SamlIssuerInfo {

	protected String name;
	protected String consumerUrl;
	protected String logoutPage;

	public SamlIssuerInfo() {

	}

	public SamlIssuerInfo(String name, String consumerUrl, String logoutPage) {
		super();
		this.name = name;
		this.consumerUrl = consumerUrl;
		this.logoutPage = logoutPage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConsumerUrl() {
		return consumerUrl;
	}

	public void setConsumerUrl(String consumerUrl) {
		this.consumerUrl = consumerUrl;
	}

	public String getLogoutPage() {
		return logoutPage;
	}

	public void setLogoutPage(String logoutPage) {
		this.logoutPage = logoutPage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SamlIssuerInfo [name=");
		builder.append(name);
		builder.append(", consumerUrl=");
		builder.append(consumerUrl);
		builder.append(", logoutPage=");
		builder.append(logoutPage);
		builder.append("]");
		return builder.toString();
	}


}
