package com.neppo.authenticatorserver.saml;
/**
 * 
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;
import java.util.zip.DataFormatException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.shiro.cache.Cache;
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
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;

import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.saml.util.SamlUtils;

import com.neppo.authenticatorserver.service.IdentityService;
import com.neppo.authenticatorserver.model.exception.UserDaoException;
import com.neppo.authenticatorserver.model.User;
import com.neppo.authenticatorserver.saml.util.Util;
import com.neppo.authenticatorserver.session.LoginSession;
import com.neppo.authenticatorserver.session.LoginSessionManager;

/**
 * @author bhlangonijr
 *
 */
public class SamlSsoServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(SamlSsoServlet.class);
	private static final long serialVersionUID = -5187271969590496989L;

	protected IdentityService identityService;
	protected Cache<String, String> cache;
	protected LoginSessionManager sessionManager;

	protected static final String loginRedirectUrl = "login.html";
	protected SAMLSignature signature;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (log.isDebugEnabled()) {
			log.debug("SAMLRequest: "+req.getParameter(SamlUtils.REQUEST));
			log.debug("RelayState:  "+req.getParameter(SamlUtils.RELAY_STATE));
		}

		String samlReq=null;
		String samlRelayState = null;
		String errorMessage = null;

		try {
			String samlReq64 = req.getParameter(SamlUtils.REQUEST);
			if (samlReq64!=null) {
				samlReq = SamlUtils.decodeMessage(samlReq64);
			}
		} catch (DataFormatException e) {
			errorMessage = "Coudn't decode SAML Request message: "+e.getMessage();
			log.error(errorMessage);
		}

		if (Util.isEmpty(samlReq)) {
			samlReq = (String) req.getSession().getAttribute(SamlUtils.REQUEST);
			if (Util.isEmpty(samlReq)) {
				errorMessage = "Invalid SAML Request Parameters";
			}
		}

		samlRelayState = req.getParameter(SamlUtils.RELAY_STATE);

		if (Util.isEmpty(samlRelayState)) {
			samlRelayState = (String) req.getSession().getAttribute(SamlUtils.RELAY_STATE);
		}

		if (log.isDebugEnabled()) {
			log.debug("SAMLRequest-decoded: "+samlReq);
		}

		AuthnRequest authnRequest = getAuthnRequest(samlReq);

		if (authnRequest == null) {
			errorMessage = "Invalid SAML Request Parameters";
		} else {
			if (!getIdentityService().getSubject().isAuthenticated()) {

				req.getSession().setAttribute(SamlUtils.REQUEST, samlReq);
				req.getSession().setAttribute(SamlUtils.RELAY_STATE, samlRelayState);

				String redirect = loginRedirectUrl;
				//req.getRequestDispatcher(redirect).forward(req, resp);

				resp.sendRedirect("./" + redirect);

				if (log.isDebugEnabled()) {
					log.debug("User session not found["+authnRequest.getID()+
							"]. Sending user to login page: "+redirect);
				}

				return;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("User session found["+authnRequest.getID());
		}

		try {
			if (authnRequest != null) {
				final String username = (String) getIdentityService().getSubject().getPrincipal();  
				//final String sessionId = (String) getIdentityService().getSubject().getSession().getId(); //malopes
				final String sessionId = req.getSession().getId();
				
				final LoginSession session = new LoginSession(sessionId, 
						authnRequest.getIssuer().getValue(), new Date(), username, samlRelayState);
				
				getLoginSessionManager().addUserSession(username, session);
			}
		} catch (Exception e) {
			log.error("Fail to add service provider to logout list: ",e);
		}

		try {
			doSsoResponse(req, resp, samlRelayState, authnRequest, errorMessage);
			req.getSession().removeAttribute(SamlUtils.REQUEST);
			req.getSession().removeAttribute(SamlUtils.RELAY_STATE);
		} catch (Exception e) {
			sendToErrorPage(resp, "Error creating SSO response", e);
		} 

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//super.doGet(req, resp);
		
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		if("malopes".equals(username) && "123".equals(password) ) {
			getIdentityService().getSubject().setAuthenticated(true);
		}
		System.out.println("IS AUTH?: " + getIdentityService().getSubject().isAuthenticated());
		
		doGet(req, resp);  //?????? malopes
	}

	// executes the sso response after user successfuly login
	protected void doSsoResponse(HttpServletRequest req, HttpServletResponse resp, 
			String relayState, AuthnRequest authnRequest, String errorMessage)
					throws Exception {

		Response response = null;
		boolean authError = false;

		if (errorMessage == null &&
				!getIdentityService().getSubject().isAuthenticated()) {
			errorMessage = "Couldn't authenticate principal";
			authError = true;
		} 

		if (errorMessage==null) {
			response = createAuthnResponse(authnRequest, req);  //malopes
		} else {
			response = createAuthnErrorResponse(authnRequest,errorMessage,authError);
		}

		String url = authnRequest.getAssertionConsumerServiceURL();

		String encodedResponse = null;

		Document doc = SamlUtils.asDOMDocument(response);
		//getSignature().signSAMLObject(doc.getDocumentElement());  //aqui!!! malopes

		String xml = SamlUtils.marshall(
				doc.getDocumentElement(),false);
		encodedResponse = Base64.encodeBytes(xml.getBytes(), 
				Base64.DONT_BREAK_LINES);

		PrintWriter out = resp.getWriter();
		resp.setContentType("text/html; charset=UTF-8");
		
		out.print(SamlUtils.RESPONSE_FORM.replace("${assertion.url}", url).
				replace("${encodedResponse}", encodedResponse).
				replace("${relayState}", ""));
		
		System.out.println(SamlUtils.RESPONSE_FORM.replace("${assertion.url}", url).
				replace("${encodedResponse}", encodedResponse).
				replace("${relayState}", ""));
		/*out.print(SamlUtils.RESPONSE_FORM.replace("${assertion.url}", url).
				replace("${encodedResponse}", encodedResponse).
				replace("${relayState}", relayState));*/
		out.close();

	}

	/*
	 * Creates the response for the authentication request
	 */
	protected Response createAuthnResponse(AuthnRequest authnRequest, HttpServletRequest req) 
			throws IOException, MarshallingException, TransformerException, UserDaoException {

		String username = (String)getIdentityService().
				getSubject().getPrincipal();
/*		String sessionIndex = getIdentityService().getSubject().
				getSession().getId().toString();*/  //malopes
		final String sessionIndex = req.getSession().getId();
		

		Response response = SamlUtils.createResponse(StatusCode.SUCCESS_URI, 
				authnRequest.getID(), SamlUtils.ISSUER_NAME_STRING);

		response.setDestination(authnRequest.getAssertionConsumerServiceURL());
		response.setID(UUID.randomUUID().toString());
		response.setVersion(SAMLVersion.VERSION_20);
		response.setIssueInstant(new DateTime());

		Assertion authnAssertion = SamlUtils.createAuthnAssertion (
				//SamlUtils.createSubject	(username, null, "holder-of-key"),
				SamlUtils.createSubject     (username, null, "bearer"),
				AuthnContext.PPT_AUTHN_CTX, authnRequest.getIssuer().getValue(),
				sessionIndex);

		AttributeStatement statement = SamlUtils.create (AttributeStatement.class, 
				AttributeStatement.DEFAULT_ELEMENT_NAME);

		User user = getIdentityService().getUser(username);

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

		authnAssertion.getStatements().add(statement);

		response.getAssertions().add(authnAssertion);	

		return response;

	}

	/*
	 * Creates the response for the authentication request
	 */
	protected Response createAuthnErrorResponse(AuthnRequest authnRequest, String errorMesssage,
			boolean authError) 
					throws IOException, MarshallingException, TransformerException, UserDaoException {

		String statusCode = authError ? StatusCode.AUTHN_FAILED_URI:StatusCode.RESPONDER_URI;

		Response response = SamlUtils.createResponse(statusCode, errorMesssage, 
				authnRequest.getID(), SamlUtils.ISSUER_NAME_STRING);

		response.setDestination(authnRequest.getAssertionConsumerServiceURL());
		response.setID(UUID.randomUUID().toString());
		response.setVersion(SAMLVersion.VERSION_20);
		response.setIssueInstant(new DateTime());

		return response;

	}
	/*
	 * gets the authnrequest stored in the web session
	 */	
	private AuthnRequest getAuthnRequest(String samlReq) {

		AuthnRequest authnRequest = null;

		try {
			if (samlReq != null) { 
				authnRequest = SamlUtils.deserializeRequest(samlReq);
				if (log.isDebugEnabled()) {
					log.debug("AuthnRequest ID: "+authnRequest.getID());
					SamlUtils.printToFile(SamlUtils.unmarshall(samlReq), null);
				}
			}
		} catch (Exception e) {
			log.error("Couldn't parse SAML Request Parameters",e);
		}
		return authnRequest;
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

	protected Cache<String,String> getCache() {
		if (cache == null) {
			cache = getIdentityService().getCacheManager().getCache("identity-server-saml");
		}
		return cache;
	}

	/**
	 * @return the identityService
	 */
	public IdentityService getIdentityService() {
		if (identityService==null) {
			ApplicationContext context = WebApplicationContextUtils.
					getRequiredWebApplicationContext(getServletContext()); 
			identityService = (IdentityService) context.getBean("identityService");
		}		

		return identityService;
	}

	public LoginSessionManager getLoginSessionManager() {
		if (sessionManager==null) {
			ApplicationContext context = WebApplicationContextUtils.
					getRequiredWebApplicationContext(getServletContext()); 
			sessionManager = (LoginSessionManager) context.getBean("loginSessionManager");
		}		

		return sessionManager;
	}


	/**
	 * @param identityService the identityService to set
	 */
	public void setIdentityService(IdentityService identityService) {
		this.identityService = identityService;
	}


}
