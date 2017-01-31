package org.opensirf.jaxrs.storage;

import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.storage.multicontainer.MultiContainerStrategy;

public class AbstractStrategyFactory {
	public static IStorageContainerStrategy createStrategy(SIRFConfiguration config) {
		if (config.getContainerConfiguration().getDriver().equalsIgnoreCase("multi")) {
			return new MultiContainerStrategy(config.getContainerConfiguration());
		}

		return null;
	}
}
