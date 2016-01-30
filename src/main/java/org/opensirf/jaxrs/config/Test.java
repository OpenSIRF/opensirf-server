package org.opensirf.jaxrs.config;

import java.nio.file.Files;
import java.nio.file.Paths;
 
public class Test {
 
    public static void main(String[] args) throws Exception  {
    	SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
    			unmarshalConfig(new String(Files.readAllBytes(Paths.get("/var/lib/sirf/confalt.json"))));
    	
    	System.out.println(new SIRFConfigurationMarshaller().marshalConfig(config));
    	
//    	JAXBContext jc = JAXBContext.newInstance(SIRFConfiguration.class);
// 
//        Unmarshaller unmarshaller = jc.createUnmarshaller();
//        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
//		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);	
//        File f = new File("/var/lib/sirf/conf.json");
//        StreamSource json = new StreamSource(f);
//        SIRFConfiguration config = unmarshaller.unmarshal(json, SIRFConfiguration.class).getValue();        
//        System.out.println(config.getContainerConfiguration().getClass());
//        
//        Marshaller marshaller = jc.createMarshaller();
//        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//        marshaller.marshal(config, System.out);
    }
 
}