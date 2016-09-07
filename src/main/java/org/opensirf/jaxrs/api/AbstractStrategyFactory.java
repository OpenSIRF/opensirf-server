package org.opensirf.jaxrs.api;

import org.opensirf.jaxrs.config.SIRFConfiguration;

public class AbstractStrategyFactory {
	public static StorageContainerStrategy createStrategy(SIRFConfiguration config) {
		if (config.getContainerConfiguration().getDriver().equalsIgnoreCase("swift")) {
			return new SwiftStrategy(config.getContainerConfiguration());
		}

		return null;
	}
}
