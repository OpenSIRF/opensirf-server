package org.opensirf.jaxrs.storage;

import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.storage.fs.FilesystemStrategy;
import org.opensirf.jaxrs.storage.multicontainer.MultiContainerStrategy;
import org.opensirf.jaxrs.storage.swift.SwiftStrategy;

public class AbstractStrategyFactory {
	public static IStorageContainerStrategy createStrategy(SIRFConfiguration config) {
		if (config.getContainerConfiguration().getDriver().equalsIgnoreCase("swift")) {
			return new SwiftStrategy(config.getContainerConfiguration());
		} else if (config.getContainerConfiguration().getDriver().equalsIgnoreCase("fs")) {
			return new FilesystemStrategy(config.getContainerConfiguration());
		} else if (config.getContainerConfiguration().getDriver().equalsIgnoreCase("multi")) {
			return new MultiContainerStrategy(config.getContainerConfiguration());
		}

		return null;
	}
}
