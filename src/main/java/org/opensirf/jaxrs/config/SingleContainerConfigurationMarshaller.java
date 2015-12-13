package org.opensirf.jaxrs.config;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;

public class SingleContainerConfigurationMarshaller {
	public SingleContainerConfigurationMarshaller(String mediaType) {
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(SingleContainerConfiguration.class);
			jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, mediaType);
			jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);			
		}
		catch(JAXBException je) {
			je.printStackTrace();
		}
	}
	
	public String marshalConfig(SingleContainerConfiguration c) throws JAXBException {
		StringWriter w = new StringWriter();
		jaxbMarshaller.marshal(c,w);
		return w.toString();
	}
	
	private Marshaller jaxbMarshaller;
}
