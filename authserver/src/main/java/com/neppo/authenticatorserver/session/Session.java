package com.neppo.authenticatorserver.session;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class Session {

	public static void addAttribute(String name, Object value){
		
		RequestContextHolder.currentRequestAttributes().setAttribute(name, value, RequestAttributes.SCOPE_SESSION);
	}
	
	public static Object getAttribute(String name) {
		
		return RequestContextHolder.currentRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_SESSION);
	}
}
