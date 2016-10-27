package com.neppo.authenticatorserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class AuthserverApplication {

	public static void main(String[] args) {

		// spring boot default
		SpringApplication.run(AuthserverApplication.class, args);

	}
}
