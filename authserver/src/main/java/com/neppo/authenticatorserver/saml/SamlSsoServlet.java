package com.neppo.authenticatorserver.saml;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;
import java.util.zip.DataFormatException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
//import org.apache.shiro.cache.Cache;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;

import com.neppo.authenticatorserver.model.SamlSsoConfig;
import com.neppo.authenticatorserver.model.Subject;
import com.neppo.authenticatorserver.model.dao.SamlSsoConfigDAO;
import com.neppo.authenticatorserver.model.exception.UserDaoException;
import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.saml.util.SamlUtils;
import com.neppo.authenticatorserver.saml.util.Util;
import com.neppo.authenticatorserver.service.IdentityService;
import com.neppo.authenticatorserver.session.LoginSession;
import com.neppo.authenticatorserver.session.LoginSessionManager;

/**
 * @author bhlangonijr
 *
 */
@WebServlet("/sso")
public class SamlSsoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SamlSsoServlet.class);

	@Autowired
	protected IdentityService identityService;
	
	@Autowired
	private SamlSsoConfigDAO samlConfigDAO;
	
	//protected Cache<String, String> cache;
	protected LoginSessionManager sessionManager;

	public static final String loginUsername = "user.email";
	public static final String loginPassword = "user.password";
	
	protected static final String loginRedirectUrl = "./login-form";
	private  static final String errorRedirectUrl = "./ops";
	protected SAMLSignature signature;
	
	private String samlReq=null;
	private String samlRelayState = null;
	private String errorMessage = null;
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		samlReq=null;
		samlRelayState = null;
		errorMessage = null;

		samlReq = getSamlRequest(req);
		AuthnRequest authnRequest = getAuthnRequestInSession(samlReq);

		if (authnRequest == null) {
			
			errorMessage = "Invalid SAML Request Parameters";
			String redirect = errorRedirectUrl + "?erro="+errorMessage;
			resp.sendRedirect("./" + redirect);
			return;
		} 
			
		SamlSsoConfig samlConfig = getSamlSsoConfig(authnRequest);
		
		if(samlConfig == null) {
			
			errorMessage = "SamlConfig not found.";
			String redirect = errorRedirectUrl + "?erro="+errorMessage;
			resp.sendRedirect("./" + redirect);
			return;
			
		} else {
			
			req.getSession().setAttribute(SamlUtils.SAMLSSOCONFIG, samlConfig);
		}
		
		if (!getIdentityService().getSubject().isAuthenticated()) {

			req.getSession().setAttribute(SamlUtils.REQUEST, samlReq);
			req.getSession().setAttribute(SamlUtils.RELAY_STATE, samlRelayState);

			String redirect = loginRedirectUrl;
			resp.sendRedirect("./" + redirect);

			if (log.isDebugEnabled()) {
				log.debug("User session not found["+authnRequest.getID()+
						"]. Sending user to login page: "+redirect);
			}
			return;
		}
		

		if (log.isDebugEnabled()) {
			log.debug("User session found["+authnRequest.getID());
		}

		try {
			if (authnRequest != null) {
				final String username = (String) getIdentityService().getSubject().getPrincipal();  
				final String sessionId = (String) getIdentityService().getSubject().getSession().getId(); 
				
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
			getIdentityService().setSubject(new Subject());  //malopes
			
		} catch (Exception e) {
			sendToErrorPage(resp, "Error creating SSO response", e);
		} 

	}

	private SamlSsoConfig getSamlSsoConfig(AuthnRequest authnRequest) {
		SamlSsoConfig samlConfig = null;
		try {
			
			samlConfig = getSamlSsoConfigDAO().findByIssuer(authnRequest.getIssuer().getValue());
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return samlConfig;
	}

	private String getSamlRequest(HttpServletRequest req) {
		
		String localSamlReq = null;
		try {
			String samlReq64 = req.getParameter(SamlUtils.REQUEST);
			if (samlReq64!=null) {
				localSamlReq = SamlUtils.decodeMessage(samlReq64);
			}
		} catch (DataFormatException e) {
			errorMessage = "Coudn't decode SAML Request message: "+e.getMessage();
			log.error(errorMessage);
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

		if (log.isDebugEnabled()) {
			log.debug("SAMLRequest-decoded: "+localSamlReq);
		}
		
		return localSamlReq;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		doGet(req, resp);
		
/*		String username = req.getParameter(loginUsername);
		String password = req.getParameter(loginPassword);
		
		User user = getIdentityService().getUser(username);
		
		if(user != null && password.equals(user.getPassword()) ) {
			
			Subject subject = new Subject();
			subject.setPrincipal(username);
			subject.setSession(new HttpServletSession(req.getSession(), req.getRemoteHost()));
			subject.setAuthenticated(true);
			
			getIdentityService().setSubject(subject);
			
			doGet(req, resp);  
		
		} else {

			//req.getSession().setAttribute(SamlUtils.REQUEST, samlReq);
			//req.getSession().setAttribute(SamlUtils.RELAY_STATE, samlRelayState);

			String redirect = loginRedirectUrl;
			resp.sendRedirect(redirect);

		}
		*/
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
			response = createAuthnResponse(authnRequest);  
		} else {
			response = createAuthnErrorResponse(authnRequest, errorMessage, authError);
		}

		String url = authnRequest.getAssertionConsumerServiceURL();

		String encodedResponse = null;

		Document doc = SamlUtils.asDOMDocument(response);
		//getSignature().signSAMLObject(doc.getDocumentElement());  //malopes

		String xml = SamlUtils.marshall(
				doc.getDocumentElement(),false);
		
		encodedResponse = Base64.encodeBytes(xml.getBytes(), 
				Base64.DONT_BREAK_LINES);

		PrintWriter out = resp.getWriter();
		resp.setContentType("text/html; charset=UTF-8");
		
		out.print(SamlUtils.RESPONSE_FORM.replace("${assertion.url}", url).
				replace("${encodedResponse}", encodedResponse).
				replace("${relayState}", ""));
		
		/*System.out.println(SamlUtils.RESPONSE_FORM.replace("${assertion.url}", url).
				replace("${encodedResponse}", encodedResponse).
				replace("${relayState}", ""));*/
		/*out.print(SamlUtils.RESPONSE_FORM.replace("${assertion.url}", url).
				replace("${encodedResponse}", encodedResponse).
				replace("${relayState}", relayState));*/
		out.close();

	}

	/*
	 * Creates the response for the authentication request
	 */
	protected Response createAuthnResponse(AuthnRequest authnRequest) 
			throws IOException, MarshallingException, TransformerException, UserDaoException {

		String username = (String)getIdentityService().
				getSubject().getPrincipal();
		String sessionIndex = getIdentityService().getSubject().
				getSession().getId().toString();  
		
		Response response = SamlUtils.createResponse(StatusCode.SUCCESS_URI, 
				authnRequest.getID(), SamlUtils.ISSUER_NAME_STRING);

		response.setDestination(authnRequest.getAssertionConsumerServiceURL());
		response.setID(UUID.randomUUID().toString());
		response.setVersion(SAMLVersion.VERSION_20);
		response.setIssueInstant(new DateTime());

		Assertion authnAssertion = SamlUtils.createAuthnAssertion (authnRequest, 
				SamlUtils.createSubject (username, null, "bearer", authnRequest), 
				AuthnContext.PASSWORD_AUTHN_CTX,  authnRequest.getDestination(),  
				sessionIndex);  			
		
/*		AttributeStatement statement = SamlUtils.create (AttributeStatement.class, 
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

		authnAssertion.getStatements().add(statement);*/

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
	private AuthnRequest getAuthnRequestInSession(String samlReq) {

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

/*	protected Cache<String,String> getCache() {
		if (cache == null) {
			cache = getIdentityService().getCacheManager().getCache("identity-server-saml");
		}
		return cache;
	}*/

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
	
	public SamlSsoConfigDAO getSamlSsoConfigDAO() {
		if (samlConfigDAO==null) {
			ApplicationContext context = WebApplicationContextUtils.
					getRequiredWebApplicationContext(getServletContext()); 
			samlConfigDAO = (SamlSsoConfigDAO) context.getBean("samlConfigDAO");
		}		

		return samlConfigDAO;
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
