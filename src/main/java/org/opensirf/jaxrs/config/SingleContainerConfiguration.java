package org.opensirf.jaxrs.config;

public class SingleContainerConfiguration extends ContainerConfiguration {
	
	public SingleContainerConfiguration() {
		
	}
	
	public SingleContainerConfiguration(Driver driver, String endpoint) {
		super();
		this.driver = driver;
		this.endpoint = endpoint;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	private Driver driver;
	private String endpoint;

	public enum Driver {
		
		SWIFT("openstack-swift");
		
		private String name;
		
		Driver(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}

}