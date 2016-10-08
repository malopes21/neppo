package com.neppo.authenticatorserver.saml;

import java.util.zip.DataFormatException;

import javax.servlet.http.HttpServletRequest;

import org.opensaml.saml2.core.AuthnRequest;

import com.neppo.authenticatorserver.domain.SamlSsoConfig;
import com.neppo.authenticatorserver.saml.util.SamlUtils;
import com.neppo.authenticatorserver.saml.util.Util;
import com.neppo.authenticatorserver.service.AuthenticationService;
import com.neppo.authenticatorserver.session.Session;

public class SamlRequest {
	
	public static void validate(AuthnRequest authnRequest, AuthenticationService authenticationService) {
		
		if (authnRequest == null) {
			throw new RuntimeException("Invalid SAML Request.");
		} 
		
		SamlSsoConfig samlConfig = authenticationService.findSamlConfig(authnRequest.getIssuer().getValue());
		
		if(!authnRequest.getIssuer().getValue().equals(samlConfig.getIssuer()))
			throw new RuntimeException("Invalid Issuer.");
		
		if(!authnRequest.getAssertionConsumerServiceURL().equals(samlConfig.getAssertionConsumerServiceURL()))
			throw new RuntimeException("Invalid assertion consumer URL.");
		
		if(!authnRequest.getDestination().equals(samlConfig.getDestination())) {
			throw new RuntimeException("Invalid saml destination.");
		}
		
	}
	
	public static AuthnRequest build(HttpServletRequest req) {
		
		AuthnRequest request = (AuthnRequest) Session.getAttribute(SamlUtils.REQUEST);
		
		if(request != null)
			return request;
		
		String stringSaml = parseSaml(req); 
		return authnRequestBuilder(stringSaml);
	}

	private static String parseSaml(HttpServletRequest req) {
		
		String localSamlReq = null;
		String samlRelayState = null;
		
		try {
			String samlReq64 = req.getParameter(SamlUtils.REQUEST);
			if (samlReq64!=null) {
				localSamlReq = SamlUtils.decodeMessage(samlReq64);
			}
		} catch (DataFormatException e) {
			throw new RuntimeException("Coudn't decode SAML Request message: "+e.getMessage());
		}

		if (Util.isEmpty(localSamlReq)) {
			localSamlReq = (String) req.getSession().getAttribute(SamlUtils.REQUEST);
			if (Util.isEmpty(localSamlReq)) {
				throw new RuntimeException("Invalid SAML Request Parameters");
			}
		}

		samlRelayState = req.getParameter(SamlUtils.RELAY_STATE);

		if (Util.isEmpty(samlRelayState)) {
			samlRelayState = (String) req.getSession().getAttribute(SamlUtils.RELAY_STATE);
		}
		
		return localSamlReq;
	}
	
	private static AuthnRequest authnRequestBuilder(String samlReq) {

		AuthnRequest authnRequest = null;

		try {
			if (samlReq != null) { 
				authnRequest = SamlUtils.deserializeRequest(samlReq);
			}
		} catch (Exception e) {
			throw new RuntimeException("Couldn't parse SAML Request Parameters, " + e.getMessage());
		}
		return authnRequest;
	}

}
