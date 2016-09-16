package org.opensirf.jaxrs.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opensirf.jaxrs.config.ContainerConfiguration.Driver;
import org.opensirf.jaxrs.storage.fs.FilesystemConfiguration;
import org.opensirf.jaxrs.storage.swift.SwiftConfiguration;

public class ContainerConfigurationAdapter extends
	XmlAdapter<ContainerConfigurationAdapter.AdaptedSIRFConfiguration, ContainerConfiguration> {

	@Override
	public AdaptedSIRFConfiguration marshal(ContainerConfiguration sirfConfiguration) {
		if(sirfConfiguration == null) {
			return null;
		}
		AdaptedSIRFConfiguration adaptedSirfConfig = new AdaptedSIRFConfiguration();
		if(sirfConfiguration instanceof SwiftConfiguration) {
			SwiftConfiguration swiftConfig = (SwiftConfiguration) sirfConfiguration;
			adaptedSirfConfig.identity = swiftConfig.getIdentity();
			adaptedSirfConfig.credential = swiftConfig.getCredential();
			adaptedSirfConfig.provider = swiftConfig.getProvider();
			adaptedSirfConfig.region = swiftConfig.getRegion();
			adaptedSirfConfig.containerName = swiftConfig.getContainerName();
			adaptedSirfConfig.driver = swiftConfig.getDriver();
			adaptedSirfConfig.endpoint = swiftConfig.getEndpoint();
		} else if(sirfConfiguration instanceof FilesystemConfiguration) {
			FilesystemConfiguration fsConfig = (FilesystemConfiguration) sirfConfiguration;
			adaptedSirfConfig.containerName = fsConfig.getContainerName();
			adaptedSirfConfig.driver = fsConfig.getDriver();
			adaptedSirfConfig.endpoint = fsConfig.getEndpoint();
			adaptedSirfConfig.mountPoint = fsConfig.getMountPoint();
		} else {
			// Do stuff for generic SIRFConfiguration
		}
		return adaptedSirfConfig;
	}

	@Override
	public ContainerConfiguration unmarshal(AdaptedSIRFConfiguration adaptedSirfConfig) throws Exception {
		if(adaptedSirfConfig == null) {
			return null;
		}
		
		// Use driver instead
		if(adaptedSirfConfig.driver.equalsIgnoreCase(Driver.SWIFT.toString())) {
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
		} else if(adaptedSirfConfig.driver.equalsIgnoreCase(Driver.FILESYSTEM.toString())) {
			FilesystemConfiguration fsConfig = new FilesystemConfiguration();
			fsConfig.setMountPoint(adaptedSirfConfig.mountPoint);
			fsConfig.setContainerName(adaptedSirfConfig.containerName);
			fsConfig.setDriver(adaptedSirfConfig.driver);
			fsConfig.setEndpoint(adaptedSirfConfig.endpoint);
			System.out.println(fsConfig.getClass().getName());
			
			return fsConfig;
		} else {
			// Do stuff for generic SIRFConfiguration
			return null;
		}
	}

	// Combine ALL attributes of ALL SIRFConfiguration subclasses here
	// Also add new classes to @XmlSeeAlso on SIRFConfiguration.java
	public static class AdaptedSIRFConfiguration {
		@XmlElement
		public String identity;

		@XmlElement
		public String mountPoint;

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
