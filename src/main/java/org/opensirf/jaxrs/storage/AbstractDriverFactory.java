package org.opensirf.jaxrs.storage;

import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.storage.fs.FilesystemDriver;
import org.opensirf.jaxrs.storage.swift.SwiftDriver;

public class AbstractDriverFactory {
	public static ISirfDriver createDriver(ContainerConfiguration config) {
		if (config.getDriver().equalsIgnoreCase("swift")) {
			return new SwiftDriver(config);
		} else if (config.getDriver().equalsIgnoreCase("fs")) {
			return new FilesystemDriver(config);
		}

		return null;
	}
}
