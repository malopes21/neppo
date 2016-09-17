package com.neppo.authenticatorserver.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neppo.authenticatorserver.model.Account;
import com.neppo.authenticatorserver.model.dao.util.HttpClientUtils;

@Service
public class AccountDAO {
	
	public Account findByUsername(String username) {
		
		try {
			List<NameValuePair> headers = new ArrayList<>();
			
			headers.add(new NameValuePair("Host", "localhost:8080"));
			headers.add(new NameValuePair("Content-Type", "application/json"));
			headers.add(new NameValuePair("Accept", "application/json"));
			String urlDest = "http://localhost:8080/provisionmanager/api/authentications/accounts/"+username;
			String response = HttpClientUtils.sendGet(urlDest, headers);
			
			ObjectMapper mapper = new ObjectMapper();
			Account obj = mapper.readValue(response, Account.class);
			return obj;
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	
		return null;
				
	}
}
