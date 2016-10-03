package com.neppo.authenticatorserver.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neppo.authenticatorserver.model.AuthenticationRequest;
import com.neppo.authenticatorserver.model.AuthenticationResponse;
import com.neppo.authenticatorserver.model.AuthenticationRule;
import com.neppo.authenticatorserver.model.dao.util.HttpClientUtils;
import com.neppo.authenticatorserver.model.exception.DaoException;
import com.neppo.authenticatorserver.model.representation.AuthenticationRequestRepresentation;
import com.neppo.authenticatorserver.model.representation.AuthenticationResponseRepresentation;
import com.neppo.authenticatorserver.service.exception.AccountNotFoundException;
import com.neppo.authenticatorserver.service.exception.AuthenticationPolicyException;
import com.neppo.authenticatorserver.service.exception.JsonParseException;

@Service
public class AuthenticationService {
	
	private static final String URL_DESTINATION_SERVICE = "http://localhost:8080/provisionmanager/api/authentications/accounts";

	public AuthenticationResponse authenticateUser(AuthenticationRequest authnData) {

		AuthenticationResponse authnResponse = tryAuthenticate(authnData);
		
		if(!authnResponse.isSucess()) {
			
			StringBuilder message = new StringBuilder();
			message.append("Authentication Policy Error! ");
			if(authnResponse.getRules() != null) {
				for(AuthenticationRule rule: authnResponse.getRules()) {
					if(!rule.isValidated()) {
						message.append("\n"+rule.getType());
					}
				}
			}
			
			throw new AuthenticationPolicyException(message.toString());
		}
		
		return authnResponse;
	}

	private AuthenticationResponse tryAuthenticate(AuthenticationRequest authnData) {


		HttpResponse response = null;
		try {
			List<NameValuePair> headers = createHttpHeaders();
			AuthenticationRequestRepresentation representation = new AuthenticationRequestRepresentation(authnData);
			response = HttpClientUtils.sendPost(URL_DESTINATION_SERVICE, headers, 
					AuthenticationRequestRepresentation.mapper(representation));

		} catch (JsonProcessingException ex) {
			
			ex.printStackTrace();
			throw new JsonParseException("Erro de conversao de objeto AuthenticationRequestRepresentation para formato JSON. " 
					+ ex.getCause().getMessage());
		
		} catch (Exception ex) {
			
			ex.printStackTrace();
			throw new DaoException("Erro de acesso a Provision Manager Authentication API. " 
					+ ex.getCause().getMessage());
		}

		if (response != null && response.getStatusLine().getStatusCode() == 404) {
			
			throw new AccountNotFoundException("Conta de usuario com username/password nao encontrada! Status:" 
					+ response.getStatusLine());
		}

		try {

			String sResponse = EntityUtils.toString(response.getEntity());
			AuthenticationResponse authnResponse = AuthenticationResponseRepresentation.build(sResponse);
			return authnResponse;
			
		} catch (Exception ex) {

			ex.printStackTrace();
			throw new JsonParseException("Erro de conversao de formato JSON para objeto AuthenticationResponseRepresentation. " 
					+ ex.getCause().getMessage());
		}

	}

	private List<NameValuePair> createHttpHeaders() {
		List<NameValuePair> headers = new ArrayList<>();

		headers.add(new NameValuePair("Host", "localhost:8080"));
		headers.add(new NameValuePair("Content-Type", "application/json"));
		headers.add(new NameValuePair("Accept", "application/json"));
		return headers;
	}

}
