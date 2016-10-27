/**
 * 
 */
package com.neppo.authenticatorserver.saml.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.impl.SecureRandomIdentifierGenerator;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.StatusMessage;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.LogoutResponseBuilder;
import org.opensaml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml2.core.impl.StatusCodeBuilder;
import org.opensaml.saml2.core.impl.StatusMessageBuilder;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.neppo.authenticatorserver.saml.SamlIssuerInfo;

/**
 * @author bhlangonijr
 *
 */
public class SamlUtils {

	private static final Logger log = Logger.getLogger(SamlUtils.class);

	public static final String REQUEST = "SAMLRequest";
	public static final String RESPONSE = "SAMLResponse";

	public static final String LOGOUT_REQUEST = "LogoutRequest";
	public static final String LOGOUT_RESPONSE = "LogoutResponse";

	public static final String RELAY_STATE = "RelayState";

	public static final String SAML_SSO_CONFIG = "samlSsoConfig";
	public static final String ISSUER_NAME_STRING = "http://localhost:8082/authserver/sso";
	public static final String ISSUER_FORMAT_STRING = "urn:oasis:names:tc:SAML:2.0:nameid-format:entity";
	public static final String NAME_ID_UNSPECIFIED = "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";
	public static final String ATTRIBUTE_URI_FORMAT = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";
	public final static String NAMEID_FORMAT = "urn:oasis:names:tc:SAML:2.0:nameid-format:entity";
	public final static String NAMEID_FORMAT2 ="urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress";
	public final static String NAMEID_PERSISTENT_FORMAT = "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent";
	public final static String SAML_ASSERTION = "urn:oasis:names:tc:SAML:2.0:assertion";
	public final static String SAML_20_PROTOCOL = "urn:oasis:names:tc:SAML:2.0:protocol";
	public final static String PASSWORD_PROTECTED_TRANSPORT = "urn:oasis:names:tc:SAML:2.0:ac:classes:Password";
	//public final static String PASSWORD_PROTECTED_TRANSPORT = "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";
	public final static String HTTP_POST_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";	
	public final static String LOGOUT_USER = "urn:oasis:names:tc:SAML:2.0:logout:user";
	public  static final String RESPONSE_FORM = "<html><body onload=\"auto();\">\n"+
			"<form id = \"saml\" method=\"post\" action=\"${assertion.url}\">\n"+
			"<input id = \"input\" type=\"hidden\" name=\"" + SamlUtils.RESPONSE + "\"/>\n"+
			"<input id = \"input2\" type=\"hidden\" name=\"" + SamlUtils.RELAY_STATE + "\"/>\n"+
			"<input type=\"hidden\" name=\"" +
			"submit1" + "\" value=\"" + "submit1"+ "\"/> </form>\n"+
			"</body></html>\n"+
			"<script> function auto() {\n"+
			"	  document.getElementById('input').value = '${encodedResponse}';\n"+
			"	  document.getElementById('input2').value = '${relayState}';\n"+
			"	  saml.submit();} </script>\n";


	private SamlUtils () {

	}

	private static final ThreadLocal<DocumentBuilder> builderLocal =
			new ThreadLocal<DocumentBuilder>() {
		@Override protected DocumentBuilder initialValue() {
			try {
				DocumentBuilderFactory factory = 
						DocumentBuilderFactory.newInstance ();
				factory.setNamespaceAware (true);
				DocumentBuilder builder = factory.newDocumentBuilder ();
				return builder;
			} catch (ParserConfigurationException e) {
				log.error("Couldn't create a new instace of the document builder: ",e);
				return null;
			}
		}
	};
	
	private static LogoutResponseBuilder responseBuilder = new LogoutResponseBuilder();
	private static IssuerBuilder issuerBuilder = new IssuerBuilder();

	private static DocumentBuilder getBuilder() {
		return builderLocal.get();
	}

	private static SecureRandomIdentifierGenerator generator;
	private static final String CM_PREFIX = "urn:oasis:names:tc:SAML:2.0:cm:";

	

	/**
	    Any use of this class assures that OpenSAML is bootstrapped.
	    Also initializes an ID generator.
	 */
	static 	{
		try	{

			DefaultBootstrap.bootstrap ();
			generator = new SecureRandomIdentifierGenerator ();
		} catch (Exception ex) {
			log.error("Error while initializing SamlUtils: ",ex);
		}
	}

	/**
	    <u>Slightly</u> easier way to create objects using OpenSAML's 
	    builder system.
	 */
	// cast to SAMLObjectBuilder<T> is caller's choice    
	@SuppressWarnings ("unchecked")
	public static <T> T create (Class<T> cls, QName qname) {
		return (T) ((XMLObjectBuilder<?>) 
				Configuration.getBuilderFactory ().getBuilder (qname))
				.buildObject (qname);
	}

	/**
	    Helper method to add an XMLObject as a child of a DOM Element.
	 */
	public static Element addToElement (XMLObject object, Element parent)
			throws IOException, MarshallingException, TransformerException {
		Marshaller out = 
				Configuration.getMarshallerFactory ().getMarshaller (object);
		return out.marshall (object, parent);
	}

	/**
	    Helper method to get an XMLObject as a DOM Document.
	 */
	public static Document asDOMDocument (XMLObject object)
			throws IOException, MarshallingException, TransformerException {
		Document document = getBuilder().newDocument ();
		Marshaller out = 
				Configuration.getMarshallerFactory ().getMarshaller (object);
		out.marshall (object, document);
		return document;
	}

	/**
	    Helper method to pretty-print any XML object to a file.
	 */
	public static void printToFile (XMLObject object, String filename)
			throws IOException, MarshallingException, TransformerException 	{
		Document document = asDOMDocument (object);

		String result = SamlPrinter.print (document);
		if (filename != null) {
			PrintWriter writer = new PrintWriter (new FileWriter (filename));
			writer.println (result);
			writer.close ();
		} else {
			log.debug(result);
		}	
	}

	/**
	    Helper method to read an XML object from a DOM element.
	 */
	public static XMLObject fromElement (Element element)
			throws IOException, UnmarshallingException, SAXException {
		return Configuration.getUnmarshallerFactory ()
				.getUnmarshaller (element).unmarshall (element);    
	}

	/**
	    Helper method to read an XML object from a file.
	 */
	public static XMLObject readFromFile (String filename)
			throws IOException, UnmarshallingException, SAXException {
		return fromElement (getBuilder().parse (filename).getDocumentElement ());    
	}

	/**
	    Helper method to spawn a new Issuer element based on our issuer URL.
	 */
	public static Issuer spawnIssuer (String issuerURL)  {
		Issuer result = null;
		if (issuerURL != null) 	{
			result = create (Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
			result.setValue (issuerURL);
			result.setFormat(ISSUER_FORMAT_STRING);
		}

		return result;
	}

	/**
	    Returns a SAML subject.

	    @param username The subject name
	    @param format If non-null, we'll set as the subject name format
	    @param confirmationMethod If non-null, we'll create a SubjectConfirmation
	        element and use this as the Method attribute
	 */

	public static Subject createSubject(String username, String format, String confirmationMethod, AuthnRequest authnRequest ) {	
	
		NameID nameID = create (NameID.class, NameID.DEFAULT_ELEMENT_NAME);
		nameID.setValue (username);
		if (format != null){
			nameID.setFormat (format);
		} else {
			nameID.setFormat(NAMEID_FORMAT2); 
		}
		
		Subject subject = create (Subject.class, Subject.DEFAULT_ELEMENT_NAME);
		subject.setNameID (nameID);

		if (confirmationMethod != null) {
			SubjectConfirmation confirmation = create 
					(SubjectConfirmation.class, 
							SubjectConfirmation.DEFAULT_ELEMENT_NAME);
			confirmation.setMethod (CM_PREFIX + confirmationMethod);
			
			SubjectConfirmationData confirmationData = create(SubjectConfirmationData.class, SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
			confirmationData.setNotOnOrAfter(new DateTime());
			confirmationData.setRecipient(authnRequest.getAssertionConsumerServiceURL());
			confirmationData.setInResponseTo(authnRequest.getID());
			
			confirmation.setSubjectConfirmationData(confirmationData);

			subject.getSubjectConfirmations().add(confirmation);
		}

		return subject;        
	}

	/**
	    Returns a SAML assertion with generated ID, current timestamp, given
	    subject, and simple time-based conditions.

	    @param subject Subject of the assertion
	 */
	public static Assertion createAssertion (AuthnRequest authnRequest, Subject subject, String issuerURL) {
		
		Assertion assertion = 
				create (Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);
		assertion.setID (generator.generateIdentifier ());

		DateTime now = new DateTime ();
		assertion.setIssueInstant (now);

		if (issuerURL != null) {
			assertion.setIssuer (spawnIssuer (issuerURL));
		}	

		assertion.setSubject (subject);

		Conditions conditions = create 
				(Conditions.class, Conditions.DEFAULT_ELEMENT_NAME);
		conditions.setNotBefore (now.minusMinutes(5) /*minusHours(1)*/);
		conditions.setNotOnOrAfter (now.plusMinutes(5) /*plusHours(3)*/);
		
		Audience audience = create(Audience.class, Audience.DEFAULT_ELEMENT_NAME);
		audience.setAudienceURI(authnRequest.getIssuer().getValue() /*"exampleidp"*/);
		
		AudienceRestriction audienceRestriction = create(AudienceRestriction.class, AudienceRestriction.DEFAULT_ELEMENT_NAME);
		audienceRestriction.getAudiences().add(audience);
		
		conditions.getAudienceRestrictions().add(audienceRestriction);
		assertion.setConditions (conditions);

		return assertion;
	}

	/**
	    Helper method to generate a response, based on a pre-built assertion.
	 */
	public static Response createResponse (Assertion assertion, String issuerURL) 
			throws IOException, MarshallingException, TransformerException 	{
		return createResponse (assertion, null, issuerURL);
	}

	/**
	    Helper method to generate a shell response with a given status code
	    and query ID.
	 */
	public static Response createResponse (String statusCode, String inResponseTo, String issuerURL) 
			throws IOException, MarshallingException, TransformerException 	{
		return createResponse (statusCode, null, inResponseTo, issuerURL);
	}

	/**
	    Helper method to generate a shell response with a given status code,
	    status message, and query ID.
	 */
	public static Response createResponse 	(String statusCode, String message, String inResponseTo, String issuerURL)
			throws IOException, MarshallingException, TransformerException 	{
		Response response = create 
				(Response.class, Response.DEFAULT_ELEMENT_NAME);
		response.setID (generator.generateIdentifier ());

		if (inResponseTo != null) {
			response.setInResponseTo (inResponseTo);
		}

		DateTime now = new DateTime ();
		response.setIssueInstant (now);

		if (issuerURL != null) {
			response.setIssuer (spawnIssuer (issuerURL));
		}

		StatusCode statusCodeElement = create 
				(StatusCode.class, StatusCode.DEFAULT_ELEMENT_NAME);
		statusCodeElement.setValue (statusCode);

		Status status = create (Status.class, Status.DEFAULT_ELEMENT_NAME);
		status.setStatusCode (statusCodeElement);
		response.setStatus (status);

		if (message != null) {
			StatusMessage statusMessage = create 
					(StatusMessage.class, StatusMessage.DEFAULT_ELEMENT_NAME);
			statusMessage.setMessage (message);
			status.setStatusMessage (statusMessage);
		}

		return response;
	}

	/**
	    Helper method to generate a response, based on a pre-built assertion
	    and query ID.
	 */
	public static Response createResponse (Assertion assertion, String inResponseTo, String issuerURL)
			throws IOException, MarshallingException, TransformerException	{
		Response response = 
				createResponse (StatusCode.SUCCESS_URI, inResponseTo, issuerURL);

		response.getAssertions ().add (assertion);

		return response;
	}

	/**
	 * Returns a SAML authentication assertion.
	 * @param subject
	 * @param authnCtx
	 * @param issuerURL
	 * @param sessionIndex
	 * @return
	 */
	public static Assertion createAuthnAssertion (AuthnRequest authnRequest, Subject subject, String authnCtx, 
			String issuerURL, String sessionIndex) {
		
		Assertion assertion = createAssertion (authnRequest, subject, issuerURL);

		AuthnContextClassRef ref = create (AuthnContextClassRef.class, 
				AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
		ref.setAuthnContextClassRef (authnCtx);

		AuthnContext authnContext = create 
				(AuthnContext.class, AuthnContext.DEFAULT_ELEMENT_NAME);
		authnContext.setAuthnContextClassRef (ref);

		AuthnStatement authnStatement = create 
				(AuthnStatement.class, AuthnStatement.DEFAULT_ELEMENT_NAME);

		authnStatement.setAuthnContext (authnContext);
		authnStatement.setAuthnInstant(new DateTime());
		authnStatement.setSessionIndex(sessionIndex);

		assertion.getStatements ().add (authnStatement);

		return assertion;
	}

	public static Attribute createAttribute(String name, String value)	{
		final XMLObjectBuilder<?> builder = 
				Configuration.getBuilderFactory ().getBuilder (XSAny.TYPE_NAME);

		XSAny valueElement = (XSAny) builder.buildObject 
				(AttributeValue.DEFAULT_ELEMENT_NAME);
		valueElement.setTextContent (value);

		Attribute attribute = create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
		attribute.setName (name);
		attribute.getAttributeValues ().add (valueElement);

		return attribute;
	}
	
	/**
	    Adds a SAML attribute to an attribute statement.

	    @param statement Existing attribute statement
	    @param name Attribute name
	    @param value Attribute value
	 */
	public static void addAttribute(AttributeStatement statement, String name, String value)	{
		statement.getAttributes().add(createAttribute(name, value));
	}
	
	public static void addAttribute(AttributeStatement statement, String name, List<String> values)	{
		for (String value: values) {
			statement.getAttributes().add(createAttribute(name, value));	
		}		
	}

	/**
	    Returns a SAML attribute assertion.

	    @param subject Subject of the assertion
	    @param attributes Attributes to be stated (may be null)
	 */
	public static Assertion createAttributeAssertion(AuthnRequest authnRequest, Subject subject, Map<String,String> attributes, String issuerURL) {
		
		Assertion assertion = createAssertion (authnRequest, subject, issuerURL);

		AttributeStatement statement = create (AttributeStatement.class, 
				AttributeStatement.DEFAULT_ELEMENT_NAME);
		if (attributes != null)
			for (Map.Entry<String,String> entry : attributes.entrySet ())
				addAttribute (statement, entry.getKey (), entry.getValue ());

		assertion.getStatements ().add (statement);

		return assertion;
	}

	public static String marshall(XMLObject xmlObject, boolean deflated) throws Exception {

		Marshaller marshaller = org.opensaml.Configuration
				.getMarshallerFactory().getMarshaller(xmlObject);

		org.w3c.dom.Element element = null;

		element = marshaller.marshall(xmlObject);

		return marshall(element, deflated);

	}

	public static String marshall(Element element, boolean deflated) throws Exception {

		String requestMessage = null;

		// Get the string
		StringWriter rspWrt = new StringWriter();
		XMLHelper.writeNode(element, rspWrt);
		requestMessage = rspWrt.toString();

		log.debug(requestMessage);

		if (!deflated) {
			return requestMessage;
		}

		// DEFLATE compression of the message, byteArrayOutputStream will holds
		// the compressed bytes
		Deflater deflater = new Deflater(Deflater.DEFLATED, true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(
				byteArrayOutputStream, deflater);

		deflaterOutputStream.write(requestMessage.getBytes());
		deflaterOutputStream.close();

		// return byteArrayOutputStream.toString();

		String encodedRequestMessage = Base64.encodeBytes(
				byteArrayOutputStream.toByteArray(), Base64.DONT_BREAK_LINES);
		
		encodedRequestMessage = URLEncoder.encode(encodedRequestMessage,"UTF-8").trim();

		return encodedRequestMessage;

	}

	public static XMLObject unmarshall(String message)
			throws ConfigurationException, ParserConfigurationException,
			SAXException, IOException, UnmarshallingException {

		DocumentBuilder docBuilder = getBuilder();

		Document document = docBuilder.parse(new ByteArrayInputStream(
				message.trim().getBytes()));

		Element element = document.getDocumentElement();

		UnmarshallerFactory unmarshallerFactory = Configuration
				.getUnmarshallerFactory();

		Unmarshaller unmarshaller = unmarshallerFactory
				.getUnmarshaller(element);

		return unmarshaller.unmarshall(element);

	}

	public static String decodeMessage(String message) throws DataFormatException {
		String msg = message;

		byte[] b = Base64.decode(msg);

		byte[] c = decompress(b);

		return new String(c);
	}

	private static byte[] decompress(byte[] data) {
		Inflater decompressor = new Inflater(true);
		decompressor.setInput(data);

		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

		byte[] buf = new byte[1024];
		try {
			while (!decompressor.finished()) {
				int count = decompressor.inflate(buf);
				bos.write(buf, 0, count);
			}
		} catch (DataFormatException e) {
			log.error(e);
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				log.error(e);
			}
		} 

		// Get the decompressed data
		return bos.toByteArray();
	}

	public static String encodeCompress(String xmlString) throws UnsupportedEncodingException {

		String encodedRequestMessage = Base64.encodeBytes(xmlString.getBytes(),
				Base64.DONT_BREAK_LINES);
		encodedRequestMessage = URLEncoder.encode(encodedRequestMessage,"UTF-8").trim();

		return encodedRequestMessage;
	}

	public static String encode(String xmlString) throws Exception {
		Deflater deflater = new Deflater(8, true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(
				byteArrayOutputStream, deflater);
		deflaterOutputStream.write(xmlString.getBytes());
		deflaterOutputStream.close();
		String encodedRequestMessage = org.opensaml.xml.util.Base64
				.encodeBytes(byteArrayOutputStream.toByteArray(), 8);
		return encodedRequestMessage.trim();
	}

	/**
	 * Serializes an authentication request into a string.
	 * 
	 * @param request the request to serialize
	 * 
	 * @return the serialized form of the string
	 * 
	 * @throws MarshallingException thrown if the request can not be marshalled and serialized
	 */
	public static String serializeRequest(AuthnRequest request) throws MarshallingException {
		Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(request);
		Element requestElem = marshaller.marshall(request);
		StringWriter writer = new StringWriter();
		XMLHelper.writeNode(requestElem, writer);
		return writer.toString();
	}

	/**
	 * Deserailizes an authentication request from a string.
	 * 
	 * @param request request to deserialize
	 * 
	 * @return the request XMLObject
	 * 
	 * @throws UnmarshallingException thrown if the request can no be deserialized and unmarshalled
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserializeRequest(String request) throws UnmarshallingException {
		try {
			DocumentBuilder docBuilder = getBuilder();
			InputSource requestInput = new InputSource(new StringReader(request));
			Element requestElem = docBuilder.parse(requestInput).getDocumentElement();
			Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(requestElem);
			return (T) unmarshaller.unmarshall(requestElem);
		} catch (Exception e) {
			throw new UnmarshallingException("Unable to read serialized authentication request");
		}
	}
	public static String buildUrlRequest(){
		String url = "{0}?SAMLRequest={1}&RelayState={2}";
		return url;
	}
	
	public static LogoutResponse createLogoutResponse(LogoutRequest request, String errorMessage) {

		LogoutResponse response = responseBuilder.buildObject();
		String issuerName = request.getIssuer().getValue();
		Issuer issuer = issuerBuilder.buildObject();
		issuer.setValue(SamlUtils.ISSUER_NAME_STRING);
		issuer.setFormat(SamlUtils.ISSUER_FORMAT_STRING);

		response.setIssuer(issuer);
		
		SamlIssuerInfo info = ConfigContext.getInstance().getSamlIssuerInfo(issuerName); 
		String url = null;
		if (info != null) {
			url=info.getLogoutPage();
		}

		response.setID(UUID.randomUUID().toString());
		response.setIssueInstant(new DateTime());
		response.setInResponseTo(request.getID());
		response.setDestination(url);

		StatusBuilder builder = new StatusBuilder();
		Status status = builder.buildObject();

		StatusCodeBuilder codeBuilder = new StatusCodeBuilder();
		StatusCode code = codeBuilder.buildObject();

		if (errorMessage != null) {
			code.setValue(StatusCode.RESPONDER_URI);
			StatusMessageBuilder builder2 = new StatusMessageBuilder();
			StatusMessage message = builder2.buildObject();
			message.setMessage(errorMessage);
			status.setStatusMessage(message);
		} else {
			code.setValue(StatusCode.SUCCESS_URI);
		}
		
		status.setStatusCode(code);
		response.setStatus(status);
		
		return response;

	}

}
