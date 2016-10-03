package com.neppo.authenticatorserver.model.dao.util;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClientUtils {

	
	public static HttpResponse sendGet(String url, List<NameValuePair> headers ) throws ClientProtocolException, IOException {
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet method = new HttpGet(url);
		
		if(headers != null) {
			for(NameValuePair pair: headers) {
				method.setHeader(pair.getName(), pair.getValue());
			}
		}
		
		HttpResponse response = client.execute(method);
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
		return response;
	}
	
}
