package org.opensirf.jaxrs.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({SwiftConfiguration.class})

// TODO: make abstract but still compatible with JAXB
public class SIRFConfiguration {
	
	public SIRFConfiguration() {
	}
	
	public SIRFConfiguration(String containerName, String driver, String endpoint) {
		this.containerName = containerName;
		this.driver = driver;
		this.endpoint = endpoint;
	}
	
	public void setContainerName(String name) {
		this.containerName = name;
	}
	
	public String getContainerName() {
		return containerName;
	}
	
	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	private String containerName;
	
	private String driver;
	
	private String endpoint;
	
	public static final String SIRF_DEFAULT_DIRECTORY = "/var/lib/sirf/";
}
