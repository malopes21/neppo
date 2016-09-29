package com.neppo.authenticatorserver.model.representation;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neppo.authenticatorserver.model.AuthenticationRule;
import com.neppo.authenticatorserver.model.AuthenticationRuleType;

public class AuthenticationRuleRepresentation extends ResourceSupport {

	@JsonInclude(Include.NON_NULL)
	private Long identifier;

	@JsonInclude(Include.NON_NULL)
	private AuthenticationRuleType type;

	@JsonInclude(Include.NON_NULL)
	private boolean validated = false;

	public AuthenticationRuleRepresentation() {
		super();
	}
	
	public AuthenticationRuleRepresentation(AuthenticationRule authnRule) {
		
		this.identifier = authnRule.getId();
		this.type = authnRule.getType();
		this.validated = authnRule.isValidated();
	}
	
	public static AuthenticationRule build(AuthenticationRuleRepresentation representation){
		
		AuthenticationRule rule = new AuthenticationRule();
		rule.setId(representation.getIdentifier());
		rule.setType(representation.getType());
		rule.setValidated(representation.isValidated());
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

}
