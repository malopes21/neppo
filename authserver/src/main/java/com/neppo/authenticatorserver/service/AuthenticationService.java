package com.neppo.authenticatorserver.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neppo.authenticatorserver.model.AuthenticationRequest;
import com.neppo.authenticatorserver.model.AuthenticationResponse;
import com.neppo.authenticatorserver.model.User;
import com.neppo.authenticatorserver.model.dao.util.HttpClientUtils;
import com.neppo.authenticatorserver.model.exception.DaoException;
import com.neppo.authenticatorserver.model.representation.AuthenticationRequestRepresentation;
import com.neppo.authenticatorserver.model.representation.AuthenticationResponseRepresentation;
import com.neppo.authenticatorserver.session.Subject;

@Service
public class AuthenticationService {

	public AuthenticationResponse authenticateUser(AuthenticationRequest authnData) {

		AuthenticationResponse authnDataResponse = authenticate(authnData);

		// retirar a validaçao de null para exception

		if (authnDataResponse != null) {
			User user = new User();
			user.setUsername(authnDataResponse.getAccount().getUsername());
			user.setEmail(authnDataResponse.getAccount().getDescription());
			user.setFirstName(authnDataResponse.getAccount().getName());
			user.setName(authnDataResponse.getAccount().getName());
			user.setSureName(authnDataResponse.getAccount().getName());
			Subject subject = Subject.authenticate(user);

			return authnDataResponse;
		}

		return null;
	}

	private AuthenticationResponse authenticate(AuthenticationRequest authnData) {

		ObjectMapper mapper = new ObjectMapper();
		HttpResponse response = null;
		try {
			List<NameValuePair> headers = new ArrayList<>();

			headers.add(new NameValuePair("Host", "localhost:8080"));
			headers.add(new NameValuePair("Content-Type", "application/json"));
			headers.add(new NameValuePair("Accept", "application/json"));
			String urlDest = "http://localhost:8080/provisionmanager/api/authentications/accounts";
			AuthenticationRequestRepresentation representation = new AuthenticationRequestRepresentation(authnData);
			response = HttpClientUtils.sendPost(urlDest, headers, mapper.writeValueAsString(representation));

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new DaoException("Erro de acesso ao Provision Manager API. " + ex.getCause().getMessage()); // mudar
		}

		if (response != null && response.getStatusLine().getStatusCode() == 404) {
			return null; // tudo em exception
		}

		try {

			String sResponse = EntityUtils.toString(response.getEntity());
			// String sResponse = HttpClientUtils.processResponse(response);

			// definir um builder
			AuthenticationResponseRepresentation authenticationResponseRepresentation = mapper.readValue(sResponse,
					AuthenticationResponseRepresentation.class);
			return AuthenticationResponseRepresentation.build(authenticationResponseRepresentation);

		} catch (Exception ex) {

			ex.printStackTrace();
			return null; // tudo em exception
		}

	}

}
