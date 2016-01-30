package org.opensirf.jaxrs.config;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;

public class SIRFConfigurationUnmarshaller {
	
	public SIRFConfiguration unmarshalConfig(String s) {
		try {
			JAXBContext jc = JAXBContext.newInstance(SIRFConfiguration.class);
			 
		    Unmarshaller unmarshaller = jc.createUnmarshaller();
		    unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
			unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);	
		    SIRFConfiguration config = unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(s.getBytes())), SIRFConfiguration.class).getValue();
		    
		    return config;
			
		} catch(JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}
    
	
}
