/**
 * 
 */
package com.neppo.authenticatorserver.saml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author bhlangonijr
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SamlIssuerInfoRecord {
	
	protected List<SamlIssuerInfo> issuerInfos;
	
	public SamlIssuerInfoRecord() {
		
	}
	
	public List<SamlIssuerInfo> getIssuerInfos() {
		if (issuerInfos==null) {
			issuerInfos = new ArrayList<SamlIssuerInfo>();
		}
		return issuerInfos;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SamlIssuerInfoRecord [issuerInfos=");
		builder.append(issuerInfos);
		builder.append("]");
		return builder.toString();
	}
	
}
