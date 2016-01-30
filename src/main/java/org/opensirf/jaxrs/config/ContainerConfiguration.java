package org.opensirf.jaxrs.config;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(ContainerConfigurationAdapter.class)


// TODO: make abstract but still compatible with JAXB
public class ContainerConfiguration {
	
	public ContainerConfiguration() {
	}
	
	public ContainerConfiguration(String containerName, String driver, String endpoint) {
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
	
	public enum Driver {
		SWIFT("swift"), EXT4("ext4");
		
		private String name;

		Driver(String name) {
			this.name = name;
		}
		
		public String toString() { return name; }
	}
}
