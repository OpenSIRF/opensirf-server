package org.opensirf.jaxrs.config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.opensirf.jaxrs.storage.fs.FilesystemConfiguration;
import org.opensirf.jaxrs.storage.swift.SwiftConfiguration;

@XmlRootElement
@XmlSeeAlso({ SwiftConfiguration.class, FilesystemConfiguration.class })
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
