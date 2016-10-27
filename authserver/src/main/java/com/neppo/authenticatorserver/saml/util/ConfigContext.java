/**
 * 
 */
package com.neppo.authenticatorserver.saml.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.neppo.authenticatorserver.saml.SamlIssuerInfo;
import com.neppo.authenticatorserver.saml.SamlIssuerInfoRecord;

/**
 * @author bhlangonijr
 *
 */
public class ConfigContext {
	
	private static final Logger log = Logger.getLogger(ConfigContext.class);
	private static final ConfigContext instance = new ConfigContext();
	private JAXBContext jaxbContext;
	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	private long scheduledDelay = 60 * 5;

	private SamlIssuerInfoRecord samlIssuerInfoRecord;
	private String samlIssuerInfoRecordFile;
	private OpenIDConfiguration openidConfig;
	private String openidConfigFile;

	private final Map<String,SamlIssuerInfo> issuerInfoMap = 
			new HashMap<String, SamlIssuerInfo>(10); 
	
	private String profilePage=IdpConstants.DEFAULT_PROFILE_PAGE;

	private ConfigContext() {
		service.schedule(new Runnable() {
			@Override
			public void run() {
				log.info("Looking for new Issuers in the record file: " + getSamlIssuerInfoRecordFile());
				if (getSamlIssuerInfoRecordFile() != null) {
					loadSamlIssuerInfoRecordFile(getSamlIssuerInfoRecordFile());
				}	
				
				log.info("Looking for new openid config: " + getOpenidConfigFile());
				if (getOpenidConfigFile() != null) {
					loadOpenidConfigFile(getOpenidConfigFile());
				}	
			}
		}, getScheduledDelay(), TimeUnit.SECONDS);
	}

	public static ConfigContext getInstance() {
		return instance;
	}

	private SamlIssuerInfoRecord getSamlIssuerInfoRecord() {
		return samlIssuerInfoRecord;
	}

	private void setSamlIssuerInfoRecord(SamlIssuerInfoRecord samlIssuerInfoRecord) {
		this.samlIssuerInfoRecord = samlIssuerInfoRecord;
	}

	public void setSamlIssuerInfoRecordFile(String samlIssuerInfoRecordFile) {
		this.samlIssuerInfoRecordFile = samlIssuerInfoRecordFile;
		loadSamlIssuerInfoRecordFile(samlIssuerInfoRecordFile);
	}

	public String getSamlIssuerInfoRecordFile() {
		return samlIssuerInfoRecordFile;
	}

	public String getOpenidConfigFile() {
		return openidConfigFile;
	}

	public void setOpenidConfigFile(String openidConfigFile) {
		this.openidConfigFile = openidConfigFile;
		loadOpenidConfigFile(openidConfigFile);
	}

	private void loadSamlIssuerInfoRecordFile(String samlIssuerInfoRecordFile) {
		try {
			Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
			SamlIssuerInfoRecord record = (SamlIssuerInfoRecord) unmarshaller.unmarshal
					(new File(samlIssuerInfoRecordFile));
			setSamlIssuerInfoRecord(record);
			issuerInfoMap.clear();
			for(SamlIssuerInfo info: getSamlIssuerInfoRecord().getIssuerInfos()) {
				issuerInfoMap.put(info.getName(), info);
			}
			log.info("Loaded Issuer info parameters: "+record);
		} catch (Exception e) {
			log.error("Couldn't load Issuer info record: ",e);
		}

	}
	
	private void loadOpenidConfigFile(String openidConfigFile) {
		try {
			Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
			OpenIDConfiguration record = (OpenIDConfiguration) unmarshaller.unmarshal
					(new File(openidConfigFile));
			setOpenidConfig(record);
			log.info("Loaded openid info parameters: "+record);
		} catch (Exception e) {
			log.error("Couldn't load openid info record: ",e);
		}

	}

	public OpenIDConfiguration getOpenidConfig() {
		return openidConfig;
	}

	public void setOpenidConfig(OpenIDConfiguration openidConfig) {
		this.openidConfig = openidConfig;
	}

	public SamlIssuerInfo getSamlIssuerInfo(String name) {
		return issuerInfoMap.get(name);
	}

	public JAXBContext getJaxbContext() {
		return jaxbContext;
	}

	public void setJaxbContext(JAXBContext jaxbContext) {
		this.jaxbContext = jaxbContext;
	}

	public long getScheduledDelay() {
		return scheduledDelay;
	}

	public void setScheduledDelay(long scheduledDelay) {
		this.scheduledDelay = scheduledDelay;
	}

	public String getProfilePage() {
		return profilePage;
	}

	public void setProfilePage(String profilePage) {
		this.profilePage = profilePage;
	}

	public void destroy() {
		service.shutdownNow();
		issuerInfoMap.clear();
	}

}
