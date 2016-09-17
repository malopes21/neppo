package com.neppo.authenticatorserver;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.Factory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.apache.shiro.mgt.SecurityManager;

import com.neppo.authenticatorserver.saml.SamlSsoServlet;
import com.neppo.authenticatorserver.service.IdentityService;
import com.neppo.authenticatorserver.service.LoginServlet;

@SpringBootApplication
public class AuthserverApplication {

	@Bean
	public IdentityService userRepository() {
	    return new IdentityService();
	}
	
	@Bean
	public ServletRegistrationBean servletSSORegistrationBean(){
	    return new ServletRegistrationBean(new SamlSsoServlet(),"/sso/*");
	}
	
	@Bean
	public ServletRegistrationBean servletLoginRegistrationBean(){
	    return new ServletRegistrationBean(new LoginServlet(),"/login/*");
	}
	
	public static void main(String[] args) {
		
		//apache shiro 
	    Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
	    SecurityManager securityManager = factory.getInstance();
	    SecurityUtils.setSecurityManager(securityManager);
		
	    //spring boot default
		SpringApplication.run(AuthserverApplication.class, args);
	}
}
