package com.neppo.authenticatorserver.session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.xml.ConfigurationException;
import org.springframework.stereotype.Service;

import com.neppo.authenticatorserver.saml.SamlIssuerInfo;
import com.neppo.authenticatorserver.saml.SamlLogoutObjectBuilder;
import com.neppo.authenticatorserver.saml.SamlLogoutRequestBuilder;
import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.saml.util.SamlUtils;
import com.neppo.authenticatorserver.saml.util.ConfigContext;

@Service
public class LoginSessionManager {

	private static final Logger log = Logger.getLogger(LoginSessionManager.class);
	private final ConcurrentHashMap<String, List<LoginSession>> map = 
			new ConcurrentHashMap<String, List<LoginSession>>();
	protected SAMLSignature signature;
	private final ExecutorService service = Executors.newCachedThreadPool();

	public void addUserSession(String username, LoginSession s) {

		getSessions(username).add(s);

	}

	public void removeUserSession(String username, LoginSession s) {

		getSessions(username).remove(s);

	}

	public void removeAllUserSessions(String username) {
		getSessions(username).clear();
	}

	public List<LoginSession> getSessions(String username) {
		List<LoginSession> l = map.get(username);

		if (l == null) {
			synchronized (this) {
				l = map.get(username);
				if (l == null) {
					l = new CopyOnWriteArrayList<LoginSession>();
					map.put(username, l);
				}
			}
		}

		return l;
	}

	public void executeSingleLogout(String username, String sessionId) {

		List<LoginSession> sessions = getSessions(username);

		for (final LoginSession session :sessions) {
			if (!session.getId().equals(sessionId)) {
				continue;
			}
			service.execute(new Runnable() {
				@Override
				public void run() {
					try {
						sendLogoutRequestToSP(session);
					} catch (Exception e) {
						log.error("Error while trying to logout service provider: ");
					}

				}
			});
		}
		getSessions(username).clear();
	}
	protected SAMLSignature getSignature() {
		if (signature == null) {
			signature = new SAMLSignature();
		}
		return signature;
	}

	private void sendLogoutRequestToSP(LoginSession session) throws ConfigurationException {

		SamlIssuerInfo info = ConfigContext.getInstance().getSamlIssuerInfo(session.getIssuer()); 
		String url=null;

		LogoutRequest logoutRequest = SamlLogoutObjectBuilder.
				buildLogoutRequest(session.getSubject(), SamlUtils.ISSUER_NAME_STRING, session.getId());

		String requestMessage = SamlLogoutRequestBuilder.buildLogoutRequest(logoutRequest);;


		if (info != null) {
			url=info.getLogoutPage();
		}

		HttpClient client = new HttpClient();
		client.getParams().setParameter("http.useragent", "Coreo Accounts");

		BufferedReader br = null;

		PostMethod method = new PostMethod(url);
		method.addParameter(SamlUtils.REQUEST, requestMessage);
		method.addParameter(SamlUtils.RELAY_STATE, session.getRelayState());

		try{
			int returnCode = client.executeMethod(method);

			if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				log.error("The Post method is not implemented by this URI");
				method.getResponseBodyAsString();
			} else {
				StringBuilder builder = new StringBuilder();
				br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
				String readLine;
				while(((readLine = br.readLine()) != null)) {
					builder.append(readLine);
					builder.append('\n');
				}
				if (log.isDebugEnabled()) {
					log.debug("SP Logout response: "+builder.toString());
				}
			}
		} catch (Exception e) {
			log.error("Error while sending logout request: ", e);
		} finally {
			method.releaseConnection();
			if(br != null) {
				try { 
					br.close(); 
				} catch (Exception fe) {

				}
			}
		}

	}


}
