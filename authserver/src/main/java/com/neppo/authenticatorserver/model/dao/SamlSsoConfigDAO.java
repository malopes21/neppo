package com.neppo.authenticatorserver.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neppo.authenticatorserver.model.SamlSsoConfig;
import com.neppo.authenticatorserver.model.dao.util.HttpClientUtils;
import com.neppo.authenticatorserver.model.exception.DaoException;

@Service(value="samlConfigDAO")
public class SamlSsoConfigDAO {
	
	public SamlSsoConfig findByIssuer(String issuer) {
		
		HttpResponse response = null;
		try {
			List<NameValuePair> headers = new ArrayList<>();
			
			headers.add(new NameValuePair("Host", "localhost:8080"));
			headers.add(new NameValuePair("Content-Type", "application/json"));
			headers.add(new NameValuePair("Accept", "application/json"));
			String urlDest = "http://localhost:8080/provisionmanager/api/authentications/samlconfigs";
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
