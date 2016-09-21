package com.neppo.authenticatorserver.model.dao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClientUtils {

	public static final int PORT = 4000;
	public static final String key = "YOUR CLIENT KEY";
	public static final String secret = "YOUR CLIENT SECRET";
	private static final String USER_AGENT = "Mozilla/5.0";
	
	
/*	public static String sendProcessGet(String url, List<NameValuePair> headers ) throws ClientProtocolException, IOException {
		
		HttpResponse response = sendGet(url, headers);
		String responseString = processResponse(response);
		return responseString;
	}*/
	
	public static HttpResponse sendGet(String url, List<NameValuePair> headers ) throws ClientProtocolException, IOException {
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet method = new HttpGet(url);
		
		if(headers != null) {
			for(NameValuePair pair: headers) {
				method.setHeader(pair.getName(), pair.getValue());
			}
		}
		
		HttpResponse response = client.execute(method);
		//client.close();
		return response;
	}
	
	
	public static HttpResponse sendPost(String url, List<NameValuePair> headers, String content ) throws ClientProtocolException, IOException {
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost method = new HttpPost(url);
		
		if(headers != null) {
			for(NameValuePair pair: headers) {
				method.setHeader(pair.getName(), pair.getValue());
			}
		}
		
		StringEntity xmlEntity = new StringEntity(content);
		method.setEntity(xmlEntity );
		
		HttpResponse response = client.execute(method);
		//client.close();
		return response;
	}
	
	
	public static String processResponse(HttpResponse response) throws UnsupportedOperationException, IOException {

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuilder result = new StringBuilder();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}


/*	public static void main(String[] args) {
		
		try {
			List<NameValuePair> headers = new ArrayList<>();
			
			String username = "administrador";
			headers.add(new NameValuePair("Host", "localhost:8080"));
			headers.add(new NameValuePair("Content-Type", "application/json"));
			headers.add(new NameValuePair("Accept", "application/json"));
			String response = HttpClientUtils.sendGet("http://localhost:8080/provisionmanager/api/authentications/"+username+"?expand=true", headers);
			
			ObjectMapper mapper = new ObjectMapper();
			Account obj = mapper.readValue(response, Account.class);
			System.out.println(obj);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	
	}*/
}
