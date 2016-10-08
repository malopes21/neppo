package com.neppo.authenticatorserver.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neppo.authenticatorserver.dao.util.HttpClientUtils;
import com.neppo.authenticatorserver.domain.AccountStatus;
import com.neppo.authenticatorserver.domain.AuthenticationPolicy;
import com.neppo.authenticatorserver.domain.AuthenticationRequest;
import com.neppo.authenticatorserver.domain.AuthenticationResponse;
import com.neppo.authenticatorserver.domain.AuthenticationRule;
import com.neppo.authenticatorserver.domain.ErrorDetails;
import com.neppo.authenticatorserver.domain.SamlSsoConfig;
import com.neppo.authenticatorserver.domain.exception.DaoException;
import com.neppo.authenticatorserver.domain.representation.AuthenticationRequestRepresentation;
import com.neppo.authenticatorserver.domain.representation.AuthenticationResponseRepresentation;
import com.neppo.authenticatorserver.service.exception.AccountStatusNotValidException;
import com.neppo.authenticatorserver.service.exception.AuthenticationPolicyException;
import com.neppo.authenticatorserver.service.exception.CredentialsNotValidException;
import com.neppo.authenticatorserver.service.exception.JsonParseException;

@Service
public class AuthenticationService {
	
	private static final String BASE_URL_SERVICE = "http://localhost:8080/provisionmanager/api";

	public AuthenticationResponse authenticateUser(AuthenticationRequest authnRequest) {

		AuthenticationResponse authnResponse = tryAuthenticate(authnRequest);
		
		if(!authnResponse.isSucess()) {
			
			if(!authnResponse.getAccount().getPassword().equals(authnRequest.getPassword())) {
				throw new CredentialsNotValidException("Conta de usuario com username/password nao encontrada! Username: " 
						+ authnRequest.getUsername(), authnResponse);
			} 
			
			if(!allValidatedRules(authnResponse)) {
				StringBuilder message = createErrorMessage(authnResponse);
				throw new AuthenticationPolicyException(message.toString());
			}
		}
		
		if(authnResponse.getAccount().getStatus() != AccountStatus.ACTIVE) {
			throw new AccountStatusNotValidException("Conta inativa!");
		}
		
		authnResponse.setSucess(true);
		return authnResponse;
	}

	
	private boolean allValidatedRules(AuthenticationResponse authnResponse) {

		if(authnResponse.getRules() != null) {
			for(AuthenticationRule rule: authnResponse.getRules()) {
				if(!rule.isValidated()) {
					return false;
				}
			}
		}
		return true;
	}


	private AuthenticationResponse tryAuthenticate(AuthenticationRequest authnData) {

		HttpResponse response = null;
		try {
			
			List<NameValuePair> headers = createHttpHeaders();
			AuthenticationRequestRepresentation representation = new AuthenticationRequestRepresentation(authnData);
			String sAuthnRequest = AuthenticationRequestRepresentation.mapper(representation);
			String urlDest = BASE_URL_SERVICE + "/authentications/accounts";
			response = HttpClientUtils.sendPost(urlDest, headers, sAuthnRequest);

		} catch (JsonProcessingException ex) {
			
			throw new JsonParseException("Erro de conversao de objeto AuthenticationRequestRepresentation para formato JSON. " 
					+ ex.getCause().getMessage());
		
		} catch(IOException ex) {
			
			throw new DaoException("Erro de acesso a Provision Manager 'Authentication' API. " 
					+ ex.getCause().getMessage());
		} 

		String sResponse = null;
		try {
			
			sResponse = EntityUtils.toString(response.getEntity());
			ErrorDetails error = new ObjectMapper().readValue(sResponse, ErrorDetails.class);
			throw new CredentialsNotValidException("Conta de usuario com username/password nao encontrada! " + 
					"\n" + error.getMessage());
			
		} catch(IOException | JsonParseException ex) {
			//lets go
		}

		AuthenticationResponse authnResponse = null;
		try {

			authnResponse = AuthenticationResponseRepresentation.build(sResponse);
			
		} catch (Exception ex) {

			throw new JsonParseException("Erro de conversao de formato JSON para objeto AuthenticationResponseRepresentation. " 
					+ ex.getMessage());
		}

		if(authnResponse != null && authnResponse.isException()) {
			
			throw new AuthenticationPolicyException("Exceção no servidor: " + authnResponse.getErrorMessage());
		}
		
		return authnResponse;

	}

	private List<NameValuePair> createHttpHeaders() {
		List<NameValuePair> headers = new ArrayList<>();

		headers.add(new NameValuePair("Host", "localhost:8080"));
		headers.add(new NameValuePair("Content-Type", "application/json"));
		headers.add(new NameValuePair("Accept", "application/json"));
		return headers;
	}
	
	public StringBuilder createErrorMessage(AuthenticationResponse authnResponse) {
		
		StringBuilder message = new StringBuilder();
		message.append("Erro na política de autenticação! ");
		if(authnResponse.getRules() != null) {
			for(AuthenticationRule rule: authnResponse.getRules()) {
				if(!rule.isValidated()) {
					message.append("\n"+rule.getValidateError());
				}
			}
		}
		return message;
	}
	
	public void updateAccountStatus(Long id, String status) {
		
		try {
			
			List<NameValuePair> headers = createHttpHeaders();
			String urlDest = BASE_URL_SERVICE + "/accounts/"+id+"/status";
			HttpClientUtils.sendPut(urlDest, headers, status);

		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	
	public AuthenticationPolicy findPolicyByIssuer(String issuer) {
		
		try {
			
			List<NameValuePair> headers = createHttpHeaders();
			String urlDest = BASE_URL_SERVICE + "/authentications/byissuer/"+issuer;
			HttpResponse response = HttpClientUtils.sendGet(urlDest, headers);
			String sResponse = EntityUtils.toString(response.getEntity());
			AuthenticationPolicy policy = new ObjectMapper().readValue(sResponse, AuthenticationPolicy.class);
			return policy;
			
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		return null;
	}
	
	public SamlSsoConfig findSamlConfig(String issuer) {
		
		HttpResponse response = null;
		try {
			List<NameValuePair> headers = createHttpHeaders();
			String urlDest = BASE_URL_SERVICE + "/authentications/samlconfigs";
			response = HttpClientUtils.sendPost(urlDest, headers, issuer);
		
		}catch(Exception ex){
			ex.printStackTrace();
			throw new DaoException("Erro de acesso ao Provision Manager API. " + ex.getMessage());
		}
		
		if(response != null && response.getStatusLine().getStatusCode() == 404){
			return null;
		}
		
		try {
			
			String sResponse = EntityUtils.toString(response.getEntity());
			ObjectMapper mapper = new ObjectMapper();
			SamlSsoConfig obj = mapper.readValue(sResponse, SamlSsoConfig.class);
			return obj;
		}catch(Exception ex) {
			
			ex.printStackTrace();
			return null;
		}
				
	}
}
