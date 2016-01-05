package org.opensirf.jaxrs.config;

public abstract class SIRFConfiguration {
	
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
