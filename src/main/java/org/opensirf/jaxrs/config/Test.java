package org.opensirf.jaxrs.config;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
 
public class Test {
 
    public static void main(String[] args) throws Exception  {
        JAXBContext jc = JAXBContext.newInstance(SIRFConfiguration.class);
 
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);	
        File f = new File("/var/lib/sirf/conf.json");
        StreamSource json = new StreamSource(f);
        SIRFConfiguration config = unmarshaller.unmarshal(json, SIRFConfiguration.class).getValue();        
        System.out.println(config.getContainerConfiguration().getClass());
        
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(config, System.out);
    }
 
}