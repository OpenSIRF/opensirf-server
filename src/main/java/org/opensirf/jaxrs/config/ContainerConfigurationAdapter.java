package org.opensirf.jaxrs.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opensirf.jaxrs.config.ContainerConfiguration.Driver;

public class ContainerConfigurationAdapter extends XmlAdapter<ContainerConfigurationAdapter.AdaptedSIRFConfiguration, ContainerConfiguration> {

	@Override
	public AdaptedSIRFConfiguration marshal(ContainerConfiguration sirfConfiguration) throws Exception {
		if (null == sirfConfiguration) {
			return null;
		}
		AdaptedSIRFConfiguration adaptedSirfConfig = new AdaptedSIRFConfiguration();
		if (sirfConfiguration instanceof SwiftConfiguration) {
			SwiftConfiguration swiftConfig = (SwiftConfiguration) sirfConfiguration;
			adaptedSirfConfig.identity = swiftConfig.getIdentity();
			adaptedSirfConfig.credential = swiftConfig.getCredential();
			adaptedSirfConfig.provider = swiftConfig.getProvider();
			adaptedSirfConfig.region = swiftConfig.getRegion();
			adaptedSirfConfig.containerName = swiftConfig.getContainerName();
			adaptedSirfConfig.driver = swiftConfig.getDriver();
			adaptedSirfConfig.endpoint = swiftConfig.getEndpoint();
		} else {
			// Do stuff for generic SIRFConfiguration
		}
		return adaptedSirfConfig;
	}

	@Override
	public ContainerConfiguration unmarshal(AdaptedSIRFConfiguration adaptedSirfConfig) throws Exception {
		if (null == adaptedSirfConfig) {
			return null;
		}
		
		// Use driver instead
		if (adaptedSirfConfig.driver.equalsIgnoreCase(Driver.SWIFT.toString())) {
			SwiftConfiguration swiftConfig = new SwiftConfiguration();
			swiftConfig.setIdentity(adaptedSirfConfig.identity);
			swiftConfig.setCredential(adaptedSirfConfig.credential);
			swiftConfig.setProvider(adaptedSirfConfig.provider);
			swiftConfig.setRegion(adaptedSirfConfig.region);
			swiftConfig.setContainerName(adaptedSirfConfig.containerName);
			swiftConfig.setDriver(adaptedSirfConfig.driver);
			swiftConfig.setEndpoint(adaptedSirfConfig.endpoint);
			System.out.println(swiftConfig.getClass().getName());
			
			return swiftConfig;
		} else {
			// Do stuff for generic SIRFConfiguration
			return null;
		}
	}

	// Combine ALL attributes of ALL SIRFConfiguration subclasses here
	public static class AdaptedSIRFConfiguration {

		@XmlElement
		public String identity;

		@XmlElement
		public String credential;

		@XmlElement
		public String provider;

		@XmlElement
		public String region;

		@XmlElement
		public String containerName;

		@XmlElement
		public String driver;

		@XmlElement
		public String endpoint;

	}
}
