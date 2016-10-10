package com.neppo.authenticatorserver.mfa.otp;

import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.neppo.authenticatorserver.mfa.otp.Base32String.DecodingException;
import com.neppo.authenticatorserver.mfa.otp.PasscodeGenerator.Signer;



public class OTPProvider {

	private static final int PIN_LENGTH = 6; 
	private static final int REFLECTIVE_PIN_LENGTH = 9; // ROTP

	/** Default passcode timeout period (in seconds) */
	public static final int DEFAULT_INTERVAL = 30;

	/** Counter for time-based OTPs (TOTP). */
	private final TotpCounter mTotpCounter = new TotpCounter(DEFAULT_INTERVAL);

	public String getNextCode(String secret)  {
		return getCurrentCode(secret, null);
	}

	private String getCurrentCode(String secret, byte[] challenge)  {
		// Account name is required.
		if (secret == null) {
			throw new RuntimeException("No secret");
		}

		long otp_state = mTotpCounter.getValueAtTime(Utilities.millisToSeconds(currentTimeMillis()));

		return computePin(secret, otp_state, challenge);
	}

	public long currentTimeMillis() {
		return System.currentTimeMillis() + getTimeCorrectionMinutes() * Utilities.MINUTE_IN_MILLIS;
	}

	public int getTimeCorrectionMinutes() {
		return 0;
	}



	/**
	 * Computes the one-time PIN given the secret key.
	 *
	 * @param secret
	 *            the secret key
	 * @param otp_state
	 *            current token state (counter or time-interval)
	 * @param challenge
	 *            optional challenge bytes to include when computing passcode.
	 * @return the PIN
	 */
	private String computePin(String secret, long otp_state, byte[] challenge)  {
		if (secret == null || secret.length() == 0) {
			throw new RuntimeException("Null or empty secret");
		}

		try {
			Signer signer = getSigningOracle(secret);
			PasscodeGenerator pcg = new PasscodeGenerator(signer,
					(challenge == null) ? PIN_LENGTH : REFLECTIVE_PIN_LENGTH);

			return (challenge == null) ? pcg.generateResponseCode(otp_state)
					: pcg.generateResponseCode(otp_state, challenge);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException("Crypto failure", e);
		}
	}
	
	static Signer getSigningOracle(String secret) {
	    try {
	      byte[] keyBytes = decodeKey(secret);
	      final Mac mac = Mac.getInstance("HMACSHA1");
	      mac.init(new SecretKeySpec(keyBytes, ""));

	      // Create a signer object out of the standard Java MAC implementation.
	      return new Signer() {
	        public byte[] sign(byte[] data) {
	          return mac.doFinal(data);
	        }
	      };
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }

	    return null;
	  }

	  private static byte[] decodeKey(String secret)  {
		  try {
			return Base32String.decode(secret);
		} catch (DecodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	  }

}
