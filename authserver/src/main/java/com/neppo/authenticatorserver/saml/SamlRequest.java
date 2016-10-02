package com.neppo.authenticatorserver.saml;

import java.util.zip.DataFormatException;

import javax.servlet.http.HttpServletRequest;

import org.opensaml.saml2.core.AuthnRequest;

import com.neppo.authenticatorserver.model.SamlSsoConfig;
import com.neppo.authenticatorserver.model.dao.SamlSsoConfigDAO;
import com.neppo.authenticatorserver.saml.util.SamlUtils;
import com.neppo.authenticatorserver.saml.util.Util;
import com.neppo.authenticatorserver.session.Session;

public class SamlRequest {
	
	public static void validate(AuthnRequest authnRequest, SamlSsoConfigDAO samlConfigDAO) {
		
		SamlSsoConfig samlConfig = samlConfigDAO.findByIssuer(authnRequest.getIssuer().getValue());
		
		if(!authnRequest.getIssuer().getValue().equals(samlConfig.getIssuer()))
			throw new RuntimeException("Invalid Issuer");
		
		if(!authnRequest.getAssertionConsumerServiceURL().equals(samlConfig.getAssertionConsumerServiceURL()))
			throw new RuntimeException("Invalid assertion consumer URL");
		
		if (authnRequest == null) {
			
			String errorMessage = "Invalid SAML Request.";
			
			throw new RuntimeException(errorMessage);
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
		
		String errorMessage = null;
		String localSamlReq = null;
		String samlRelayState = null;
		
		try {
			String samlReq64 = req.getParameter(SamlUtils.REQUEST);
			if (samlReq64!=null) {
				localSamlReq = SamlUtils.decodeMessage(samlReq64);
			}
		} catch (DataFormatException e) {
			errorMessage = "Coudn't decode SAML Request message: "+e.getMessage();
			//log.error(errorMessage);
		}

		if (Util.isEmpty(localSamlReq)) {
			localSamlReq = (String) req.getSession().getAttribute(SamlUtils.REQUEST);
			if (Util.isEmpty(localSamlReq)) {
				errorMessage = "Invalid SAML Request Parameters";
			}
		}

		samlRelayState = req.getParameter(SamlUtils.RELAY_STATE);

		if (Util.isEmpty(samlRelayState)) {
			samlRelayState = (String) req.getSession().getAttribute(SamlUtils.RELAY_STATE);
		}

		/*if (log.isDebugEnabled()) {
			log.debug("SAMLRequest-decoded: "+localSamlReq);
		}*/
		
		return localSamlReq;
	}
	
	private static AuthnRequest authnRequestBuilder(String samlReq) {

		AuthnRequest authnRequest = null;

		try {
			if (samlReq != null) { 
				authnRequest = SamlUtils.deserializeRequest(samlReq);
				/*if (log.isDebugEnabled()) {
					log.debug("AuthnRequest ID: "+authnRequest.getID());
					SamlUtils.printToFile(SamlUtils.unmarshall(samlReq), null);
				}*/
			}
		} catch (Exception e) {
			//log.error("Couldn't parse SAML Request Parameters",e);
		}
		return authnRequest;
	}

}
