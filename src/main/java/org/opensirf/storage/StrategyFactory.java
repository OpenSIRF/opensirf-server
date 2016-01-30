package org.opensirf.storage;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.config.ContainerConfigurationUnmarshaller;
import org.opensirf.jaxrs.config.SIRFConfiguration;

public class StrategyFactory {
	public static StorageContainerStrategy createStrategy(SIRFConfiguration config) {

		if (config.getContainerConfiguration().getDriver().equalsIgnoreCase("swift")) {
			System.out.println("USING SWIFT DRIVER");
			return new SwiftStrategy(config.getContainerConfiguration());
		}

		return null;

	}
	
//	public static StorageContainerStrategy createStrategy(String containerName) {
//		try {
//			ContainerConfiguration c = new ContainerConfigurationUnmarshaller("application/json").
//					unmarshalConfig(new FileInputStream(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"));
//			
//			if(c.getDriver().equalsIgnoreCase("swift")) {
//				System.out.println("USING SWIFT DRIVER");
//				return new SwiftStrategy(c);
//			}
//			
//		} catch(IOException ioe) {
//			ioe.printStackTrace();
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//		
//		return null;
//		
//	}
}
