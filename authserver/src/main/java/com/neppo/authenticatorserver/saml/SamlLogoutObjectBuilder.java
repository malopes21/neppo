package com.neppo.authenticatorserver.saml;

import java.util.UUID;

import org.joda.time.DateTime;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.SessionIndex;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.SessionIndexBuilder;
import org.opensaml.xml.ConfigurationException;

import com.neppo.authenticatorserver.saml.util.SamlUtils;

public class SamlLogoutObjectBuilder {
	
	public static LogoutRequest buildLogoutRequest(String subject, String issuer, String indexSession) throws ConfigurationException {
				
		LogoutRequest logoutReq = (new org.opensaml.saml2.core.impl.LogoutRequestBuilder()).buildObject();
		logoutReq.setID(UUID.randomUUID().toString());
		
		DateTime issueInstant = new DateTime();
		logoutReq.setIssueInstant(issueInstant);
		logoutReq.setNotOnOrAfter(new DateTime(issueInstant.getMillis() + 5 * 60 * 1000));
		
		IssuerBuilder issuerBuilder = new IssuerBuilder();
		Issuer issueR = issuerBuilder.buildObject();
		issueR.setValue(issuer);
		logoutReq.setIssuer(issueR);
		
		NameID nameId = (new NameIDBuilder()).buildObject();
		nameId.setFormat(SamlUtils.NAMEID_FORMAT);
		nameId.setValue(subject);
		logoutReq.setNameID(nameId);
		
		SessionIndex sessionIndex = (new SessionIndexBuilder()).buildObject();
		sessionIndex.setSessionIndex(indexSession);
		logoutReq.getSessionIndexes().add(sessionIndex);
		
		logoutReq.setReason(SamlUtils.LOGOUT_USER);
		
		return logoutReq;
	}

}