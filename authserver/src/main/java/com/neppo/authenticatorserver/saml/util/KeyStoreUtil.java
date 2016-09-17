package com.neppo.authenticatorserver.saml.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Keystore utility functions
 * 
 * @author bhlangonijr
 *
 */

public class KeyStoreUtil {

	private static final Logger log = Logger.getLogger(KeyStoreUtil.class);
	private static final String endLine = System.getProperty ("line.separator");

	/**
    Get a KeyStore object given the keystore filename and password.
	 */
	public static KeyStore getKeyStore (String filename, String password) 
			throws KeyStoreException {
		KeyStore result = KeyStore.getInstance (KeyStore.getDefaultType ());

		try {
			FileInputStream in = new FileInputStream (filename);
			result.load (in, password.toCharArray ());
			in.close ();
		} catch (Exception ex) {
			log.error("Failed to read keystore["+filename+"]:",ex);
		}

		return result;
	}

	/**
    Get a KeyStore object given the keystore filename and password.
	 */
	public static KeyStore getKeyStore (InputStream in, String password) 
			throws KeyStoreException {
		KeyStore result = KeyStore.getInstance (KeyStore.getDefaultType ());

		try {
			result.load (in, password.toCharArray ());
		} catch (Exception ex) {
			log.error("Failed to read keystore: ",ex);
		}

		return result;
	}

	/**
    List all the key and certificate aliases in the keystore.

    @return A list of Strings
	 */
	public static List<String> getAliases (KeyStore keystore)
			throws KeyStoreException {
		return Collections.list (keystore.aliases ());
	}

	/**
    Get a private key from the keystore by name and password.
	 */
	public static Key getKey (KeyStore keystore, String alias, String password)
			throws GeneralSecurityException {
		return keystore.getKey (alias, password.toCharArray ());
	}

	/**
    Get a certificate from the keystore by name.
	 */
	public static java.security.cert.Certificate getCertificate(KeyStore keystore, String alias) 
			throws GeneralSecurityException {
		return keystore.getCertificate (alias);
	}

	/**
    Dump all data about the private key to the console.
	 */
	public static String spillBeans (Key key) {
		StringBuffer buffer = new StringBuffer ("Algorithm: " + 
				key.getAlgorithm () + endLine +	"Key value: " + endLine);

		appendHexValue (buffer, key.getEncoded ());

		return buffer.toString ();
	}

	/**
    Dump all data about the certificate to the console.
	 */
	public static String spillBeans (java.security.cert.Certificate cert)
			throws GeneralSecurityException {
		StringBuffer buffer = new StringBuffer("Certificate type: " + cert.getType () + endLine +
				"Encoded data: " + endLine);
		appendHexValue (buffer, cert.getEncoded ());

		return buffer.toString ();
	}

	/**
    Helper method that converts a single byte to a hex string representation.

    @param b byte Byte to convert
    @return StringBuffer with the two-digit hex string
	 */
	public static void appendHexValue (StringBuffer buffer, byte b) {
		int[] digits = { (b >>> 4) & 0x0F, b & 0x0F };
		for (int d = 0; d < digits.length; ++d) 	{
			int increment = (int) ((digits[d] < 10) ? '0' : ('a' - 10));
			buffer.append ((char) (digits[d] + increment));
		}
	}

	/**
    Helper that appends a hex representation of a byte array to an
    existing StringBuffer.
	 */
	public static void appendHexValue (StringBuffer buffer, byte[] bytes) {
		for (int i = 0; i < bytes.length; ++i)
			appendHexValue (buffer, bytes[i]);
	}

}

