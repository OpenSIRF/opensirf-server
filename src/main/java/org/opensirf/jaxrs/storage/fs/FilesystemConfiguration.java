package org.opensirf.jaxrs.storage.fs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opensirf.jaxrs.config.ContainerConfiguration;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FilesystemConfiguration extends ContainerConfiguration {

	private String mountPoint;

	public FilesystemConfiguration() {
		super();
	}
	
	public FilesystemConfiguration(String containerName, String driver, String endpoint,
			String mountPoint) {
		super(containerName, driver, endpoint);
		this.mountPoint = mountPoint;
	}

	public String getMountPoint() {
		return mountPoint;
	}

	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}
}
