package org.opensirf.storage;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.config.SIRFConfigurationUnmarshaller;

public class StrategyFactory {
	public static StorageContainerStrategy createStrategy(String containerName) {
		try {
			SIRFConfiguration c = new SIRFConfigurationUnmarshaller("application/json").
					unmarshalConfig(new FileInputStream(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"));
			
			if(c.getDriver().equalsIgnoreCase("swift"))
				return new SwiftStrategy();
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
}
