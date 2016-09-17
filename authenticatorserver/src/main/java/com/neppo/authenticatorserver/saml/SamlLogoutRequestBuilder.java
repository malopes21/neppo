package com.neppo.authenticatorserver.saml;

import org.apache.log4j.Logger;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.xml.ConfigurationException;

import com.neppo.authenticatorserver.saml.util.SamlUtils;


public class SamlLogoutRequestBuilder {
	private static Logger log = Logger.getLogger(SamlLogoutRequestBuilder.class);
	public static String  buildLogoutRequest(LogoutRequest logoutRequest) throws ConfigurationException {
			
		String message = null;
		
		try {
			message = SamlUtils.marshall(logoutRequest,true);
		} catch (Exception e) {
			log.error("Error logout request: ",e);
		} 
		
		return message;
		
	}

}