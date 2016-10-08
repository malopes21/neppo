package com.neppo.authenticatorserver.domain.representation;

import java.util.Map;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neppo.authenticatorserver.domain.AuthenticationRule;
import com.neppo.authenticatorserver.domain.AuthenticationRuleType;

public class AuthenticationRuleRepresentation extends ResourceSupport {

	@JsonInclude(Include.NON_NULL)
	private Long identifier;

	@JsonInclude(Include.NON_NULL)
	private AuthenticationRuleType type;

	@JsonInclude(Include.NON_NULL)
	private boolean validated = false;

	@JsonInclude(Include.NON_NULL)
	private String validateError;

	@JsonInclude(Include.NON_NULL)
	private Map<String, Object> params;

	public AuthenticationRuleRepresentation() {
		super();
	}

	public AuthenticationRuleRepresentation(AuthenticationRule authnRule) {

		this.identifier = authnRule.getId();
		this.type = authnRule.getType();
		this.validated = authnRule.isValidated();
		this.validateError = authnRule.getValidateError();
		this.params = authnRule.getParams();
	}

	public static AuthenticationRule build(AuthenticationRuleRepresentation representation) {

		AuthenticationRule rule = new AuthenticationRule();
		rule.setId(representation.getIdentifier());
		rule.setType(representation.getType());
		rule.setValidated(representation.isValidated());
		rule.setValidateError(representation.getValidateError());
		rule.setParams(representation.getParams());
		return rule;
	}

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public AuthenticationRuleType getType() {
		return type;
	}

	public void setType(AuthenticationRuleType type) {
		this.type = type;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public String getValidateError() {
		return validateError;
	}

	public void setValidateError(String validateError) {
		this.validateError = validateError;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

}
