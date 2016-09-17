package com.neppo.authenticatorserver.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neppo.authenticatorserver.model.SamlSsoConfig;
import com.neppo.authenticatorserver.model.dao.util.HttpClientUtils;

@Service(value="samlConfigDAO")
public class SamlSsoConfigDAO {
	
	public SamlSsoConfig findByIssuer(String issuer) {
		
		try {
			List<NameValuePair> headers = new ArrayList<>();
			
			headers.add(new NameValuePair("Host", "localhost:8080"));
			headers.add(new NameValuePair("Content-Type", "application/json"));
			headers.add(new NameValuePair("Accept", "application/json"));
			String urlDest = "http://localhost:8080/provisionmanager/api/authentications/samlconfigs/"+issuer;
			String response = HttpClientUtils.sendGet(urlDest, headers);
			
			ObjectMapper mapper = new ObjectMapper();
			SamlSsoConfig obj = mapper.readValue(response, SamlSsoConfig.class);
			return obj;
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	
		return null;
				
	}
}
