package com.neppo.authenticatorserver.service.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.neppo.authenticatorserver.domain.AuthenticationRule;
import com.neppo.authenticatorserver.domain.AuthenticationRuleType;
import com.neppo.authenticatorserver.domain.representation.exception.AuthenticationRuleParameterException;
import com.neppo.authenticatorserver.service.AbstractAuthenticationRule;
import com.neppo.authenticatorserver.service.AuthenticationRuleValidator;

public class AuthenticationRuleCheckValidIpOrigin extends AbstractAuthenticationRule implements AuthenticationRuleValidator {

	private String ipOrigin;
	
	public AuthenticationRuleCheckValidIpOrigin(AuthenticationRule rule, String ipOrigin) {
		this.setRule(rule);
		this.ipOrigin = ipOrigin;
	}
	
	@Override
	public void validate() {
		
		String mask = (String) getRule().getParams().get("mask");
		String network = (String) getRule().getParams().get("network");
		
		//TODO: loggar e ver adiante
		if(isIpv6(ipOrigin)) {
			getRule().setValidated(true);
			return;
		}
		
		boolean res = ipBelongsToNetwork(ipOrigin, network, mask);
		getRule().setValidated(res);
		
		if(!getRule().isValidated()) {
			getRule().setValidateError("IP de origem não válido para login nesse contexto!");
		}
	}

	@Override
	public AuthenticationRuleType getType() {
		return getRule().getType();
	}
	
	
	public boolean ipBelongsToNetwork(String ipAddress, String networkAddress, String mask) {

		if (!validateformat(ipAddress))
			throw new RuntimeException("Invalid IP address");

		if (!validateformat(networkAddress))
			throw new RuntimeException("Invalid network address");

		if (!validateformat(mask))
			throw new RuntimeException("Invalid mask");

		String[] ip = ipAddress.split("\\.");
		String[] m = mask.split("\\.");

		int oct1 = Integer.valueOf(ip[0]) & Integer.valueOf(m[0]);
		int oct2 = Integer.valueOf(ip[1]) & Integer.valueOf(m[1]);
		int oct3 = Integer.valueOf(ip[2]) & Integer.valueOf(m[2]);
		int oct4 = Integer.valueOf(ip[3]) & Integer.valueOf(m[3]);

		String networkFound = String.format("%s.%s.%s.%s", oct1, oct2, oct3, oct4);

		return networkFound.equals(networkAddress);
	}

	private boolean validateformat(String input) {		
		
		if(input == null) {
			return false;
		}
		
		Pattern pattern = Pattern.compile(
				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		Matcher matcher = pattern.matcher(input);

		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isIpv6(String address) {
		
		String IPV6_HEX4DECCOMPRESSED_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?) ::((?:[0-9A-Fa-f]{1,4}:)*)(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
		String IPV6_6HEX4DEC_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}:){6,6})(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
		String IPV6_HEXCOMPRESSED_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)\\z";
		String IPV6_REGEX = "\\A(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\z";
		
		boolean res1 = matcher(IPV6_HEX4DECCOMPRESSED_REGEX, address);
		boolean res2 = matcher(IPV6_6HEX4DEC_REGEX, address);
		boolean res3 = matcher(IPV6_HEXCOMPRESSED_REGEX, address);
		boolean res4 = matcher(IPV6_REGEX, address);
		
		return res1 | res2 | res3 | res4;
		
	}
	
	private boolean matcher(String format, String input) {		Pattern pattern = Pattern.compile(format);
		Matcher matcher = pattern.matcher(input);

		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void validateParams() {
		
		try {
			String mask = (String) getRule().getParams().get("mask");
			String network = (String) getRule().getParams().get("network");
			
			if (!validateformat(network))
				throw new RuntimeException("Invalid network address");

			if (!validateformat(mask))
				throw new RuntimeException("Invalid mask");
			
		}catch(Exception ex) {
			
			throw new AuthenticationRuleParameterException(ex.getMessage());
		}
	}


}
