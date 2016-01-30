package org.opensirf.jaxrs.config;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

public class SIRFConfigurationMarshaller {
	public String marshalConfig(SIRFConfiguration c) {
		try {
			JAXBContext jc = JAXBContext.newInstance(SIRFConfiguration.class);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			Marshaller marshaller = jc.createMarshaller();
	        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
	        marshaller.marshal(c, bos);
	        
	        return bos.toString();
			
		} catch(JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}
}
