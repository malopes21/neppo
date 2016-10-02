/**
 * 
 */
package com.neppo.authenticatorserver.saml;

import javax.servlet.http.HttpServlet;

/**
 * @author bhlangonijr
 *
 */
public class SamlSingleLogoutServlet extends HttpServlet {

/*	private static final Logger log = Logger.getLogger(SamlSingleLogoutServlet.class);
	private static final long serialVersionUID = -5187271969594996989L;

	//protected IdentityService identityService;
	protected static final String loginRedirectUrl = "login.html";
	protected SAMLSignature signature;
	protected LoginSessionManager sessionManager;

	public SamlSingleLogoutServlet() {

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (log.isDebugEnabled()) {
			log.debug("LogoutRequest: "+req.getParameter(SamlUtils.REQUEST));
		}

		LogoutRequest logoutRequest = null;
		String encodedResponse = null;
		String samlRelayState = req.getParameter(SamlUtils.RELAY_STATE);
		String url="";
		String errorMessage = null;

		try {

			req.getSession().removeAttribute(SamlUtils.REQUEST);
			req.getSession().removeAttribute(SamlUtils.RELAY_STATE);

			logoutRequest = (LogoutRequest) 
					SamlUtils.unmarshall(
							SamlUtils.decodeMessage(req.getParameter(SamlUtils.REQUEST)));

			String issuer = logoutRequest.getIssuer().getValue();

			SamlIssuerInfo info = ConfigContext.getInstance().getSamlIssuerInfo(issuer); 

			if (info != null) {
				url=info.getLogoutPage();
			}

			final String username = (String)getIdentityService().getSubject().getPrincipal();

			if (username != null) {
				getLoginSessionManager().executeSingleLogout(username, 
						(String)getIdentityService().getSubject().getSession().getId());
				getIdentityService().getSubject().logout();
			}

		} catch (Exception e) {
			errorMessage = "Couldn't process SAML logout request: "+e.getMessage();
			log.error("Couldn't process SAML logout request: ", e);
		}

		try {
			LogoutResponse logoutResponse = SamlUtils.createLogoutResponse(logoutRequest, errorMessage);

			Document doc = SamlUtils.asDOMDocument(logoutResponse);
			getSignature().signSAMLObject(doc.getDocumentElement());

			encodedResponse = SamlUtils.marshall(
					doc.getDocumentElement(),false);
			encodedResponse = Base64.encodeBytes(encodedResponse.getBytes(), 
                            Base64.DONT_BREAK_LINES);

		} catch (Exception e) {
			log.error("Couldn't create SAML logout response: ", e);
		}

		PrintWriter out = resp.getWriter();
		resp.setContentType("text/html; charset=UTF-8");
		out.print(SamlUtils.RESPONSE_FORM.replace("${assertion.url}", url).
				replace("${encodedResponse}", encodedResponse).
				replace("${relayState}", samlRelayState));
		out.close();

	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

	protected SAMLSignature getSignature() {
		if (signature == null) {
			signature = new SAMLSignature();
		}
		return signature;
	}


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


	public void setIdentityService(IdentityService identityService) {
		this.identityService = identityService;
	}
*/
}
