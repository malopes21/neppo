package com.neppo.authenticatorserver.controller;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Document;

import com.neppo.authenticatorserver.domain.exception.DaoException;
import com.neppo.authenticatorserver.saml.SamlRequest;
import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.saml.util.SamlUtils;
import com.neppo.authenticatorserver.service.AuthenticationService;
import com.neppo.authenticatorserver.session.LoginSessionManager;
import com.neppo.authenticatorserver.session.Session;
import com.neppo.authenticatorserver.session.Subject;

@Controller
public class SamlSsoController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SamlSsoController.class);

	@Autowired
	protected AuthenticationService authenticationService;
	
	@Autowired
	protected LoginSessionManager sessionManager;

	public static final String loginUsername = "user.email";
	public static final String loginPassword = "user.password";
	
	protected static final String loginRedirectUrl = "./login-form";
	private  static final String errorRedirectUrl = "./ops";
	protected SAMLSignature signature;
	

	@RequestMapping(value="/sso")
    public void signOn(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		String samlRelayState = null;	
		String errorMessage = null;

		AuthnRequest authnRequest = SamlRequest.build(req);
		
		Session.addAttribute(SamlUtils.REQUEST, authnRequest);
		Session.addAttribute(SamlUtils.RELAY_STATE, samlRelayState);

		try {
			
			SamlRequest.validate(authnRequest, authenticationService);
			
		} catch (Exception e) { 
			e.printStackTrace();
			String redirect = errorRedirectUrl + "?erro="+e.getMessage();
			resp.sendRedirect("./" + redirect);
			return;
		}
		
		if (isLoggedUser() == false) {   
			
			if (log.isDebugEnabled()) {
				log.debug("User session not found[" + authnRequest.getID() + "]. Sending user to login page: " + loginRedirectUrl);
			}
			
			authenticateUser(req, resp);

		} else {
			
			if (log.isDebugEnabled()) {
				log.debug("User session found[" + authnRequest.getID());
			}

			//add here session user in session manager for single logout!
			
			try {
				
				sendResponse(req, resp, samlRelayState, authnRequest, errorMessage);
				
			} catch (Exception e) {
				sendToErrorPage(resp, "Error creating SSO response", e);
			} 
		}
		
	}
	
	private void authenticateUser(HttpServletRequest request, HttpServletResponse response) {

		try {
			response.sendRedirect("./" + loginRedirectUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	private boolean isLoggedUser() {
		
		if (Subject.getLoggedUser() == null) {   
			return false;
		}
		return true;
	}


	protected void sendResponse(HttpServletRequest req, HttpServletResponse resp, 
			String relayState, AuthnRequest authnRequest, String errorMessage)
					throws Exception {

		Response response = null;
		boolean authError = false;

		if (errorMessage == null &&	Subject.getLoggedUser() == null) {
			errorMessage = "Couldn't authenticate principal";
			authError = true;
		} 

		if (errorMessage==null) {
			response = createAuthnResponse(authnRequest);  
		} else {
			response = createAuthnErrorResponse(authnRequest, errorMessage, authError);
		}

		String url = authnRequest.getAssertionConsumerServiceURL();
		String encodedResponse = null;
		Document doc = SamlUtils.asDOMDocument(response);
		
		//getSignature().signSAMLObject(doc.getDocumentElement());  

		String xml = SamlUtils.marshall(
				doc.getDocumentElement(),false);
		
		encodedResponse = Base64.encodeBytes(xml.getBytes(), 
				Base64.DONT_BREAK_LINES);

		PrintWriter out = resp.getWriter();
		resp.setContentType("text/html; charset=UTF-8");
		
		if(relayState == null) relayState = "";
		out.print(SamlUtils.RESPONSE_FORM.replace("${assertion.url}", url).
				replace("${encodedResponse}", encodedResponse).
				replace("${relayState}", relayState));
		
		out.close();
		req.getSession().removeAttribute(SamlUtils.REQUEST);
		req.getSession().removeAttribute(SamlUtils.RELAY_STATE);

	}


	protected Response createAuthnResponse(AuthnRequest authnRequest) 
			throws IOException, MarshallingException, TransformerException, DaoException {

		String username = Subject.getLoggedUser().getUsername(); 
		String sessionIndex = Subject.getSessionId(); 
		
		Response response = SamlUtils.createResponse(StatusCode.SUCCESS_URI, authnRequest.getID(), SamlUtils.ISSUER_NAME_STRING);

		response.setDestination(authnRequest.getAssertionConsumerServiceURL());
		response.setID(UUID.randomUUID().toString());
		response.setVersion(SAMLVersion.VERSION_20);
		response.setIssueInstant(new DateTime());

		Assertion authnAssertion = SamlUtils.createAuthnAssertion (
				authnRequest, 
				SamlUtils.createSubject (username, null, "bearer", authnRequest), 
				AuthnContext.PASSWORD_AUTHN_CTX,  
				authnRequest.getDestination(),  
				sessionIndex);  			
		
		AttributeStatement statment = createUserAttributes();
		authnAssertion.getStatements().add(statment);
		
		response.getAssertions().add(authnAssertion);	
		return response;
	}


	private AttributeStatement createUserAttributes() {
				
		AttributeStatement statement = SamlUtils.create (AttributeStatement.class, AttributeStatement.DEFAULT_ELEMENT_NAME);
	
		/*
		User user = getIdentityService().getAccount(username);
		SamlUtils.addAttribute(statement, "uid", username);
		SamlUtils.addAttribute(statement, "givenName", user.getFirstName());
		SamlUtils.addAttribute(statement, "sn", user.getSureName());
		SamlUtils.addAttribute(statement, "cn", user.getName());
		SamlUtils.addAttribute(statement, "mail", user.getEmail());
		SamlUtils.addAttribute(statement, "eduPersonTargetedID", username);
		SamlUtils.addAttribute(statement, "urn:oid:0.9.2342.19200300.100.1.1", user.getEmail());
		SamlUtils.addAttribute(statement, "urn:oid:2.5.4.42", user.getFirstName());
		SamlUtils.addAttribute(statement, "urn:oid:2.5.4.4", user.getSureName());
		SamlUtils.addAttribute(statement, "urn:oid:2.5.4.3", user.getName());
		SamlUtils.addAttribute(statement, "urn:oid:0.9.2342.19200300.100.1.3", user.getEmail());
		SamlUtils.addAttribute(statement, "urn:oid:1.3.6.1.4.1.5923.1.1.1.6", username);
		SamlUtils.addAttribute(statement, "urn:oid:1.3.6.1.4.1.5923.1.1.1.10", " ");
		 */
		
		return statement;
	}

	
	protected Response createAuthnErrorResponse(AuthnRequest authnRequest, String errorMesssage,
			boolean authError) 
					throws IOException, MarshallingException, TransformerException, DaoException {

		String statusCode = authError ? StatusCode.AUTHN_FAILED_URI:StatusCode.RESPONDER_URI;

		Response response = SamlUtils.createResponse(statusCode, errorMesssage, 
				authnRequest.getID(), SamlUtils.ISSUER_NAME_STRING);

		response.setDestination(authnRequest.getAssertionConsumerServiceURL());
		response.setID(UUID.randomUUID().toString());
		response.setVersion(SAMLVersion.VERSION_20);
		response.setIssueInstant(new DateTime());

		return response;
	}


	protected void sendToErrorPage(HttpServletResponse resp,
			String errorMsg, Throwable e) throws IOException {
		if (e==null) {
			log.error(errorMsg);
			resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, errorMsg);
		} else {
			log.error(errorMsg,e);
			resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, errorMsg+": "+e.getMessage());
		}
	}

	protected void sendToErrorPage(HttpServletResponse resp,
			String errorMsg) throws IOException {
		sendToErrorPage(resp, errorMsg, null);
	}

	protected SAMLSignature getSignature() {
		if (signature == null) {
			signature = new SAMLSignature();
		}
		return signature;
	}

}
