package org.opensirf.jaxrs.storage.swift;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opensirf.jaxrs.config.ContainerConfiguration;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SwiftConfiguration extends ContainerConfiguration {

	private String identity;
	private String credential;
	private String provider;
	private String region;

	public SwiftConfiguration() {
		super();
	}
	
	public SwiftConfiguration(String containerName, String driver, String endpoint, String identity,
			String credential, String provider, String region) {
		super(containerName, driver, endpoint);
		this.identity = identity;
		this.credential = credential;
		this.provider = provider;
		this.region = region;
	}

	public String getIdentity() {
		return identity;
	}

	public String getCredential() {
		return credential;
	}

	public String getProvider() {
		return provider;
	}

	public String getRegion() {
		return region;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public static final String DEFAULT_IDENTITY_PORT = "5000";
}
