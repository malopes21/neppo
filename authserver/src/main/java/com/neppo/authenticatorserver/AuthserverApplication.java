package com.neppo.authenticatorserver;

//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.config.IniSecurityManagerFactory;
//import org.apache.shiro.util.Factory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class AuthserverApplication {


	public static void main(String[] args) {
		
		/*apache shiro 
	    Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
	    SecurityManager securityManager = factory.getInstance();
	    SecurityUtils.setSecurityManager(securityManager);*/
		
	    //spring boot default
		SpringApplication.run(AuthserverApplication.class, args);
		
	}
}
