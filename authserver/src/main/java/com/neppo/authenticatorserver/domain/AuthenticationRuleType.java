package com.neppo.authenticatorserver.domain;

public enum AuthenticationRuleType {

	CHECK_REMEMBER,
	CHECK_MASTER_ACCOUNT,
	CHECK_NEW_USER_DEVICE,
	CHECK_VALID_SCHEDULE,
	CHECK_VALID_IP_ORIGIN,
	CHECK_THROTTLING,
	CHECK_MFA
	
}
