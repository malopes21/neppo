package com.neppo.authenticatorserver.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neppo.authenticatorserver.model.Account;
import com.neppo.authenticatorserver.model.AuthenticationData;
import com.neppo.authenticatorserver.model.dao.util.HttpClientUtils;
import com.neppo.authenticatorserver.model.exception.DaoException;
import com.neppo.authenticatorserver.model.representation.AuthenticationDataRepresentation;

@Service
public class AccountDAO {
	
	public Account findByAuthenticationData(AuthenticationData authnData) {
		
		ObjectMapper mapper = new ObjectMapper();
		HttpResponse response = null;
		try {
			List<NameValuePair> headers = new ArrayList<>();
			
			headers.add(new NameValuePair("Host", "localhost:8080"));
			headers.add(new NameValuePair("Content-Type", "application/json"));
			headers.add(new NameValuePair("Accept", "application/json"));
			String urlDest = "http://localhost:8080/provisionmanager/api/authentications/accounts";
			AuthenticationDataRepresentation representation = new AuthenticationDataRepresentation(authnData);
			response = HttpClientUtils.sendPost(urlDest, headers, mapper.writeValueAsString(representation));
		
		}catch(Exception ex){
			ex.printStackTrace();
			throw new DaoException("Erro de acesso ao Provision Manager API. " + ex.getCause().getMessage());
		}
		
		if(response != null && response.getStatusLine().getStatusCode() == 404){
			return null;
		}
		
		try {
			
			String sResponse = HttpClientUtils.processResponse(response);
			Account obj = mapper.readValue(sResponse, Account.class);
			return obj;
		}catch(Exception ex) {
			
			ex.printStackTrace();
			return null;
		}
		
	}
}
