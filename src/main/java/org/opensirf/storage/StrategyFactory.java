package org.opensirf.storage;

import org.opensirf.jaxrs.config.SIRFConfiguration;

public class StrategyFactory {
	public static StorageContainerStrategy createStrategy(SIRFConfiguration config) {
		if (config.getContainerConfiguration().getDriver().equalsIgnoreCase("swift")) {
			return new SwiftStrategy(config.getContainerConfiguration());
		}

		return null;
	}
}
