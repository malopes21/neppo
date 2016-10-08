package com.neppo.authenticatorserver.domain.representation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neppo.authenticatorserver.domain.AuthenticationPolicy;

public class AuthenticationPolicyRepresentation extends ResourceSupport {

	@JsonInclude(Include.NON_NULL)
	private Long identifier;

	@JsonInclude(Include.NON_NULL)
	private String name;

	@JsonInclude(Include.NON_NULL)
	private String description;

	@JsonInclude(Include.NON_NULL)
	private String rules;

	@JsonInclude(Include.NON_NULL)
	private List<AuthenticationRuleRepresentation> rulesList;

	@JsonInclude(Include.NON_NULL)
	private String script;

	public AuthenticationPolicyRepresentation() {
	}

	public AuthenticationPolicyRepresentation(AuthenticationPolicy AuthenticationPolicy) {
		this(AuthenticationPolicy, false);
	}

	public AuthenticationPolicyRepresentation(AuthenticationPolicy AuthenticationPolicy, Boolean expand) {

		this.identifier = AuthenticationPolicy.getId();
		this.name = AuthenticationPolicy.getName();
		this.description = AuthenticationPolicy.getDescription();
		this.rules = AuthenticationPolicy.getRules();
		
	}

	public static AuthenticationPolicy build(AuthenticationPolicyRepresentation representation) {

		AuthenticationPolicy AuthenticationPolicy = new AuthenticationPolicy();
		AuthenticationPolicy.setId(representation.getIdentifier());
		AuthenticationPolicy.setName(representation.getName());
		AuthenticationPolicy.setDescription(representation.getDescription());
		AuthenticationPolicy.setRules(representation.getRules());

		if (representation.getRulesList() != null) {
			AuthenticationPolicy.setRulesList(new ArrayList<>());
			for (AuthenticationRuleRepresentation pr : representation.getRulesList()) {
				AuthenticationPolicy.getRulesList().add(AuthenticationRuleRepresentation.build(pr));
			}
		}
		
		return AuthenticationPolicy;
	}

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<AuthenticationRuleRepresentation> getRulesList() {
		return rulesList;
	}

	public void setRulesList(List<AuthenticationRuleRepresentation> rulesList) {
		this.rulesList = rulesList;
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

}