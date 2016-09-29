package com.neppo.authenticatorserver.model;

import java.util.Map;

public class AuthenticationRule {

	private Long id;

	private AuthenticationRuleType type;

	private Map<String, Object> params;

	private boolean validated = false;

	public AuthenticationRuleType getType() {
		return type;
	}

	public void setType(AuthenticationRuleType type) {
		this.type = type;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "AuthenticationRule [type=" + type + ", params=" + params + "]";
	}

}
