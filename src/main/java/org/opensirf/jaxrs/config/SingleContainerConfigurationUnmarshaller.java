package org.opensirf.jaxrs.config;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.opensirf.format.VersionIdentifierListener;

public class SingleContainerConfigurationUnmarshaller {
	public SingleContainerConfigurationUnmarshaller(String mediaType) {
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(SingleContainerConfiguration.class);
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		    jaxbUnmarshaller.setListener(new VersionIdentifierListener());
			jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, mediaType);
			jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);			
		}
		catch(JAXBException je) {
			je.printStackTrace();
		}
	}
	
	public SingleContainerConfiguration unmarshalCatalog(InputStream is) throws JAXBException {
		return (SingleContainerConfiguration) jaxbUnmarshaller.unmarshal(new StreamSource(is), SingleContainerConfiguration.class).getValue();
	}
	
	private Unmarshaller jaxbUnmarshaller;
}
