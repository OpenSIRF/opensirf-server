package org.opensirf.jaxrs.config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlSeeAlso(SwiftConfiguration.class)
public class SIRFConfiguration {
	private ContainerConfiguration containerConfiguration;

	public ContainerConfiguration getContainerConfiguration() {
		return containerConfiguration;
	}

	public void setContainerConfiguration(ContainerConfiguration c) {
		this.containerConfiguration = c;
	}
	
	public static final String SIRF_DEFAULT_DIRECTORY = "/var/lib/sirf/";
}
