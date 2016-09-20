package com.neppo.authenticatorserver.model.dao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neppo.authenticatorserver.model.Account;

public class HttpClientUtils {

	public static final int PORT = 4000;
	public static final String key = "YOUR CLIENT KEY";
	public static final String secret = "YOUR CLIENT SECRET";

	private static final String USER_AGENT = "Mozilla/5.0";
	private String cookies;
	
	public static String sendGet(String url, List<NameValuePair> headers ) throws ClientProtocolException, IOException{
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet method = new HttpGet(url);
		
		if(headers != null) {
			for(NameValuePair pair: headers) {
				method.setHeader(pair.getName(), pair.getValue());
			}
		}
		
		HttpResponse response = client.execute(method);
		String responseString = processResponse(response);
		client.close();
		return responseString;
	}
	
	
	private static String processResponse(HttpResponse response) throws UnsupportedOperationException, IOException {

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuilder result = new StringBuilder();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}


	/**
	 * 	Not used
	 */
	
	public static String sendPost(String url, List<NameValuePair> postParams) throws Exception {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("Host", "app.box.com");
		post.setHeader("User-Agent", USER_AGENT);
		post.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Language", "en-US,en;q=0.5");
		//post.setHeader("Cookie", getCookies());
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Referer",
				"https://app.box.com/api/oauth2/authorize?response_type=code&client_id="+ key + "&redirect_uri=http%3A//localhost%3A" + PORT);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		//post.setEntity(new UrlEncodedFormEntity(postParams));

		HttpResponse response = client.execute(post);

		int responseCode = response.getStatusLine().getStatusCode();

		//System.out.println("\nSending 'POST' request to URL : " + url);
		//System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		/*setCookies(response.getFirstHeader("Set-Cookie") == null ? ""
				: response.getFirstHeader("Set-Cookie").toString());*/
		
		//get response headers
		String code = "";
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			//System.out.println("[Response Header] Name: " + header.getName() + " Value: " + header.getValue());
			if (header.getName().equals("Location")){
				code = header.getValue().substring(header.getValue().indexOf("code=")+5);
			}
		}
		/*if (getAuthCode) 
			return code;
		else*/ 	
		
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
